package br.com.sisaudcon.projeto.SAAMCND.scheduler;

import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.service.CndResultadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfExtractionScheduledTaskTest {

    @Mock
    private CndResultadoService cndResultadoService;

    @InjectMocks
    private PdfExtractionScheduledTask pdfExtractionScheduledTask;

    private CndResultado resultado1;
    private CndResultado resultado2_pdfCorrompido;
    private CndResultado resultado3_semDadosNoPdf;

    @BeforeEach
    void setUp() {
        resultado1 = new CndResultado();
        resultado1.setId(1L);
        // Simula um PDF com dados extraíveis
        String pdfContent1 = "Certidão Negativa de Débitos\n" +
                             "Data de Emissão: 01/01/2024\n" +
                             "Válida até: 30/06/2024\n" +
                             "Código de Controle: ABC123XYZ";
        resultado1.setArquivo(pdfContent1.getBytes(StandardCharsets.UTF_8));
        resultado1.setStatusProcessamento("CONSULTA_REALIZADA"); // Status que o job procura
        resultado1.setSituacao(null); // Garante que a situação não está preenchida

        resultado2_pdfCorrompido = new CndResultado();
        resultado2_pdfCorrompido.setId(2L);
        // Simula um PDF que não pode ser lido pelo PDFBox (ex: não é um PDF válido)
        // PDFBox load lançaria IOException. Para teste unitário do scheduler,
        // o mock do service ou uma forma de injetar o erro seria melhor,
        // mas aqui vamos simular que o getArquivo retorna algo que causa erro no PDDocument.load
        // No teste real do método privado, passamos o texto diretamente.
        // Para o teste do método público do scheduler, o comportamento do PDDocument.load é mais difícil de mockar diretamente.
        // Vamos assumir que o serviço salvará o erro.
        resultado2_pdfCorrompido.setArquivo("não é um pdf".getBytes(StandardCharsets.UTF_8));
        resultado2_pdfCorrompido.setStatusProcessamento("CONSULTA_REALIZADA");
        resultado2_pdfCorrompido.setSituacao(null);


        resultado3_semDadosNoPdf = new CndResultado();
        resultado3_semDadosNoPdf.setId(3L);
        String pdfContent3 = "Este PDF não contém os dados esperados.";
        resultado3_semDadosNoPdf.setArquivo(pdfContent3.getBytes(StandardCharsets.UTF_8));
        resultado3_semDadosNoPdf.setStatusProcessamento("CONSULTA_REALIZADA");
        resultado3_semDadosNoPdf.setSituacao(null);

    }

    @Test
    void processarResultadosParaExtracaoDePdf_quandoHaResultados_processaTodos() {
        when(cndResultadoService.findResultadosParaExtracao()).thenReturn(Arrays.asList(resultado1, resultado3_semDadosNoPdf));
        // Não precisamos mockar PDDocument.load() aqui, pois o método extrairEPreencherDados
        // será chamado com o texto extraído. O teste foca na lógica do scheduler.
        // O PDFBox é testado indiretamente se o texto for realmente extraído.

        pdfExtractionScheduledTask.processarResultadosParaExtracaoDePdf();

        // Verifica se o serviço de salvar foi chamado para cada resultado
        verify(cndResultadoService, times(1)).salvarResultado(resultado1);
        verify(cndResultadoService, times(1)).salvarResultado(resultado3_semDadosNoPdf);

        // Verifica o status e os dados de resultado1 (extração bem-sucedida)
        assertEquals("EXTRACAO_CONCLUIDA", resultado1.getStatusProcessamento());
        assertEquals("Negativa de Débitos", resultado1.getSituacao());
        assertEquals(LocalDate.of(2024, 1, 1), resultado1.getDataEmissao());
        assertEquals(LocalDate.of(2024, 6, 30), resultado1.getDataValidade());
        assertEquals("ABC123XYZ", resultado1.getCodigoControle());
        assertTrue(resultado1.getLinha().contains("UPDATE-PEC-4963-SUCESSO"));

        // Verifica o status de resultado3 (dados não encontrados)
        assertEquals("ERRO_EXTRACAO_DADOS_NAO_ENCONTRADOS", resultado3_semDadosNoPdf.getStatusProcessamento());
        assertTrue(resultado3_semDadosNoPdf.getLinha().contains("UPDATE-PEC-4963-ERRO_DADOS_NAO_ENCONTRADOS"));
    }

    @Test
    void processarResultadosParaExtracaoDePdf_comPdfAusente_marcaErro() {
        CndResultado resultadoPdfAusente = new CndResultado();
        resultadoPdfAusente.setId(4L);
        resultadoPdfAusente.setArquivo(null); // PDF ausente
        resultadoPdfAusente.setStatusProcessamento("CONSULTA_REALIZADA");
        resultadoPdfAusente.setSituacao(null);

        when(cndResultadoService.findResultadosParaExtracao()).thenReturn(Collections.singletonList(resultadoPdfAusente));

        pdfExtractionScheduledTask.processarResultadosParaExtracaoDePdf();

        verify(cndResultadoService, times(1)).salvarResultado(resultadoPdfAusente);
        assertEquals("ERRO_EXTRACAO_PDF_AUSENTE", resultadoPdfAusente.getStatusProcessamento());
        assertTrue(resultadoPdfAusente.getLinha().contains("UPDATE-PEC-4963-ERRO_PDF_AUSENTE"));
    }


    @Test
    void processarResultadosParaExtracaoDePdf_quandoNaoHaResultados_naoFazNada() {
        when(cndResultadoService.findResultadosParaExtracao()).thenReturn(Collections.emptyList());

        pdfExtractionScheduledTask.processarResultadosParaExtracaoDePdf();

        verify(cndResultadoService, never()).salvarResultado(any(CndResultado.class));
    }

    // Testes mais detalhados para o método privado `extrairEPreencherDados` poderiam ser feitos
    // se ele fosse tornado package-private ou usando PowerMockito para testar métodos privados,
    // ou refatorando-o para uma classe separada de "ExtratorDeDadosPdf".
    // Por enquanto, o teste acima cobre o fluxo principal do scheduler.
}
