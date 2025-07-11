package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.repository.CndResultadoRepository;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndResultadoDTO;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CndResultadoServiceTest {

    @Mock
    private CndResultadoRepository cndResultadoRepository;

    @Mock
    private CndClienteService cndClienteService;

    @InjectMocks
    private CndResultadoService cndResultadoService;

    private CndCliente cliente;
    private CndResultado resultado;
    private CndResultadoDTO resultadoDTO;

    @BeforeEach
    void setUp() {
        cliente = new CndCliente();
        cliente.setId(1L);
        cliente.setCnpj("12.345.678/0001-99");

        resultado = new CndResultado();
        resultado.setId(1L);
        resultado.setCliente(cliente);
        resultado.setSituacao("Negativa");
        resultado.setDataEmissao(LocalDate.now());
        // Conteúdo "teste" em base64 é "dGVzdGU="
        resultado.setArquivo(Base64.getDecoder().decode("dGVzdGU="));
        resultado.setStatusProcessamento("CONCLUIDO");

        resultadoDTO = new CndResultadoDTO();
        resultadoDTO.setFkCliente(1L);
        resultadoDTO.setSituacao("Positiva");
        resultadoDTO.setDataEmissao(LocalDate.now().minusDays(1));
        resultadoDTO.setArquivo("dGVzdA=="); // "test" in Base64
        resultadoDTO.setLinha("INSERT-PEC-TESTE");
        resultadoDTO.setStatusProcessamento("PENDENTE_EXTRACAO");
    }

    @Test
    void criarCndResultado_comDadosValidos_retornaDTO() {
        // Ensure a valid Base64 string for this specific test if it differs from setup
        resultadoDTO.setArquivo("dGVzdA==");
        when(cndClienteService.getClienteEntityById(1L)).thenReturn(cliente);
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> {
            CndResultado r = invocation.getArgument(0);
            r.setId(2L); // Simula save
            return r;
        });

        CndResultadoDTO novoResultadoDTO = cndResultadoService.criarCndResultado(resultadoDTO);

        assertNotNull(novoResultadoDTO);
        assertEquals(2L, novoResultadoDTO.getId());
        assertEquals("Positiva", novoResultadoDTO.getSituacao());
        assertEquals("INSERT-PEC-TESTE", novoResultadoDTO.getLinha());
        assertEquals(1L, novoResultadoDTO.getFkCliente());
        verify(cndResultadoRepository, times(1)).save(any(CndResultado.class));
    }

    @Test
    void criarCndResultado_comLinhaNula_defineLinhaDefault() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64
        resultadoDTO.setLinha(null);
        when(cndClienteService.getClienteEntityById(1L)).thenReturn(cliente);
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndResultadoDTO novoResultadoDTO = cndResultadoService.criarCndResultado(resultadoDTO);
        assertEquals("INSERT-PEC-XXXX", novoResultadoDTO.getLinha());
    }

    @Test
    void criarCndResultado_comStatusProcessamentoNulo_defineDefault() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64
        resultadoDTO.setStatusProcessamento(null);
        when(cndClienteService.getClienteEntityById(1L)).thenReturn(cliente);
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CndResultadoDTO novoResultadoDTO = cndResultadoService.criarCndResultado(resultadoDTO);
        assertEquals("PENDENTE_EXTRACAO", novoResultadoDTO.getStatusProcessamento());
    }


    @Test
    void criarCndResultado_semFkCliente_lancaBadRequestException() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64, though not directly tested here, good practice
        resultadoDTO.setFkCliente(null);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cndResultadoService.criarCndResultado(resultadoDTO);
        });
        assertEquals("ID do Cliente (fkCliente) é obrigatório para criar um resultado de CND.", exception.getMessage());
    }

    @Test
    void listarCndResultados_semFiltros_retornaLista() {
        when(cndResultadoRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(resultado));
        List<CndResultadoDTO> resultados = cndResultadoService.listarCndResultados(Collections.emptyMap());
        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("Negativa", resultados.get(0).getSituacao());
    }

    @Test
    void listarCndResultados_comFiltroFkCliente_retornaListaFiltrada() {
        Map<String, String> filters = new HashMap<>();
        filters.put("fkCliente", "1");

        when(cndResultadoRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(resultado));
        List<CndResultadoDTO> resultados = cndResultadoService.listarCndResultados(filters);

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals(1L, resultados.get(0).getFkCliente());
        // A verificação da Specification é mais complexa, confiamos que o mock está correto para o teste unitário do serviço.
        // Testes de integração para o controller validariam a query real.
    }


    @Test
    void buscarCndResultadoPorId_quandoExiste_retornaDTO() {
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        CndResultadoDTO encontrado = cndResultadoService.buscarCndResultadoPorId(1L);
        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
    }

    @Test
    void buscarCndResultadoPorId_quandoNaoExiste_lancaResourceNotFoundException() {
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cndResultadoService.buscarCndResultadoPorId(1L));
    }

    @Test
    void atualizarCndResultado_comDadosValidos_retornaDTOAtualizado() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        resultadoDTO.setLinha("UPDATE-PEC-4537-TEST"); // Linha específica para atualização
        CndResultadoDTO atualizado = cndResultadoService.atualizarCndResultado(1L, resultadoDTO);

        assertNotNull(atualizado);
        assertEquals("Positiva", atualizado.getSituacao());
        assertEquals("UPDATE-PEC-4537-TEST", atualizado.getLinha());
        verify(cndResultadoRepository, times(1)).save(any(CndResultado.class));
    }

    @Test
    void atualizarCndResultado_comLinhaNula_defineLinhaDefaultUpdate() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64
        resultadoDTO.setLinha(null);
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndResultadoDTO atualizado = cndResultadoService.atualizarCndResultado(1L, resultadoDTO);
        assertEquals("UPDATE-PEC-4537", atualizado.getLinha());
    }


    @Test
    void atualizarCndResultado_mudandoCliente_buscaNovoCliente() {
        resultadoDTO.setArquivo("dGVzdA=="); // Ensure valid Base64
        resultadoDTO.setFkCliente(2L);
        CndCliente novoCliente = new CndCliente();
        novoCliente.setId(2L);

        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        when(cndClienteService.getClienteEntityById(2L)).thenReturn(novoCliente);
        when(cndResultadoRepository.save(any(CndResultado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CndResultadoDTO atualizado = cndResultadoService.atualizarCndResultado(1L, resultadoDTO);

        assertEquals(2L, atualizado.getFkCliente());
        verify(cndClienteService, times(1)).getClienteEntityById(2L);
    }

    @Test
    void deletarCndResultado_quandoExiste_deleta() {
        when(cndResultadoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cndResultadoRepository).deleteById(1L);
        cndResultadoService.deletarCndResultado(1L);
        verify(cndResultadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarCndResultado_quandoNaoExiste_lancaResourceNotFoundException() {
        when(cndResultadoRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> cndResultadoService.deletarCndResultado(1L));
    }

    @Test
    void downloadPdf_quandoArquivoExiste_retornaBytes() {
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        byte[] pdfBytes = cndResultadoService.downloadPdf(1L);
        assertNotNull(pdfBytes);
        assertEquals("dGVzdGU=", Base64.getEncoder().encodeToString(pdfBytes));
        assertEquals("teste", new String(pdfBytes));
    }

    @Test
    void downloadPdf_quandoArquivoNaoExisteOuVazio_lancaResourceNotFoundException() {
        resultado.setArquivo(null);
        when(cndResultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        assertThrows(ResourceNotFoundException.class, () -> cndResultadoService.downloadPdf(1L));
    }


    @Test
    void gerarNomeArquivoPadronizado_comTipoFederal_retornaNomeCorreto() {
        resultado.setTipoCertidao("Federal");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_%s_RFB_%s.pdf", cnpjEsperado, dataEmissaoEsperada), nomeArquivo);
    }

    @Test
    void gerarNomeArquivoPadronizado_comTipoEstadual_retornaNomeCorreto() {
        resultado.setTipoCertidao("Estadual");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_%s_EST_%s.pdf", cnpjEsperado, dataEmissaoEsperada), nomeArquivo);
    }

    @Test
    void gerarNomeArquivoPadronizado_comTipoMunicipal_retornaNomeCorreto() {
        resultado.setTipoCertidao("Municipal");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_%s_MUN_%s.pdf", cnpjEsperado, dataEmissaoEsperada), nomeArquivo);
    }

    @Test
    void gerarNomeArquivoPadronizado_comTipoDesconhecidoComOrgaoEmissor_retornaNomeComOrgaoEmissor() {
        resultado.setTipoCertidao("OutroTipo");
        resultado.setOrgaoEmissor("SEFAZ-RJ");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_%s_SEFAZ-RJ_%s.pdf", cnpjEsperado, dataEmissaoEsperada), nomeArquivo); // Ajustado para remover não alfanuméricos
    }

    @Test
    void gerarNomeArquivoPadronizado_comTipoNuloComOrgaoEmissor_retornaNomeComOrgaoEmissor() {
        resultado.setTipoCertidao(null);
        resultado.setOrgaoEmissor("DETRAN-SP");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_%s_DETRAN-SP_%s.pdf", cnpjEsperado, dataEmissaoEsperada), nomeArquivo); // Ajustado
    }


    @Test
    void gerarNomeArquivoPadronizado_comDataEmissaoNula_retornaNomeComSemData() {
        resultado.setDataEmissao(null);
        resultado.setTipoCertidao("Federal");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String cnpjEsperado = cliente.getCnpj().replaceAll("[^0-9]", "");
        assertEquals(String.format("CND_%s_RFB_SEM_DATA.pdf", cnpjEsperado), nomeArquivo);
    }

    @Test
    void gerarNomeArquivoPadronizado_comCnpjNuloNoCliente_retornaNomeComCnpjNaoDisponivel() {
        cliente.setCnpj(null);
        resultado.setCliente(cliente);
        resultado.setTipoCertidao("Federal");
        String nomeArquivo = cndResultadoService.gerarNomeArquivoPadronizado(resultado);
        String dataEmissaoEsperada = resultado.getDataEmissao().toString();
        assertEquals(String.format("CND_CNPJ_NAO_DISPONIVEL_RFB_%s.pdf", dataEmissaoEsperada), nomeArquivo);
    }

    @Test
    void findResultadosParaExtracao_chamaRepositorioCorretamente() {
        List<CndResultado> mockList = Arrays.asList(new CndResultado());
        when(cndResultadoRepository.findByStatusProcessamentoAndSituacaoIsNull("CONSULTA_REALIZADA")).thenReturn(mockList);

        List<CndResultado> resultadosParaExtracao = cndResultadoService.findResultadosParaExtracao();

        assertEquals(mockList, resultadosParaExtracao);
        verify(cndResultadoRepository, times(1)).findByStatusProcessamentoAndSituacaoIsNull("CONSULTA_REALIZADA");
    }
}
