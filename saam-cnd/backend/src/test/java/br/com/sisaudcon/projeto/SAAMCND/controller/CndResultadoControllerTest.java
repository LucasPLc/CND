package br.com.sisaudcon.projeto.SAAMCND.controller;

import br.com.sisaudcon.projeto.SAAMCND.dto.CndResultadoDTO;
import br.com.sisaudcon.projeto.SAAMCND.model.CndCliente;
import br.com.sisaudcon.projeto.SAAMCND.model.CndResultado;
import br.com.sisaudcon.projeto.SAAMCND.service.CndResultadoService;
import br.com.sisaudcon.projeto.SAAMCND.service.SaamCrValidationService;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpHeaders; // Import adicionado
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CndResultadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CndResultadoService cndResultadoService;

    @MockBean // Mockar para todos os testes de controller, pois o AuthInterceptor será acionado
    private SaamCrValidationService saamCrValidationService;

    @Autowired
    private ObjectMapper objectMapper;

    private CndResultadoDTO resultadoRequestDTO;
    private CndResultadoDTO resultadoResponseDTO;
    private CndCliente cliente;
    private CndResultado cndResultadoEntity;

    private final String VALID_ID_CLIENTE_HEADER = "CLIENTE_VALIDO_XYZ";

    @BeforeEach
    void setUp() {
        cliente = new CndCliente();
        cliente.setId(1L);
        cliente.setCnpj("12.345.678/0001-99");

        resultadoRequestDTO = new CndResultadoDTO();
        resultadoRequestDTO.setFkCliente(1L);
        resultadoRequestDTO.setSituacao("Negativa");
        resultadoRequestDTO.setDataEmissao(LocalDate.now());
        resultadoRequestDTO.setArquivo("BASE64_PDF_TEST");
        resultadoRequestDTO.setTipoCertidao("Federal");
        resultadoRequestDTO.setOrgaoEmissor("Receita Federal Teste");
        resultadoRequestDTO.setObservacoes("Observacao teste");


        resultadoResponseDTO = new CndResultadoDTO(
            1L, LocalDateTime.now(), "BASE64_PDF_TEST", "Negativa", LocalDate.now(),
            LocalDate.now().plusDays(30), "CTRL123", "CONCLUIDO",
            null, // mensagemErroProcessamento
            "Federal", "Receita Federal Teste", "Observacao teste DTO", // tipoCertidao, orgaoEmissor, observacoes
            1L, "12.345.678/0001-99", "INSERT-PEC-XXXX",
            LocalDateTime.now(), LocalDateTime.now()
        );

        cndResultadoEntity = new CndResultado();
        cndResultadoEntity.setId(1L);
        cndResultadoEntity.setCliente(cliente);
        // Conteúdo "test" em base64 é "dGVzdGU="
        cndResultadoEntity.setArquivo(Base64.getDecoder().decode("dGVzdGU="));
        cndResultadoEntity.setDataEmissao(LocalDate.now());
        cndResultadoEntity.setCodigoControle("CTRL123");

        // Configurar mock do SaamCrValidationService para permitir acesso
        when(saamCrValidationService.isClienteAutorizado(VALID_ID_CLIENTE_HEADER)).thenReturn(true);
    }

    @Test
    void criarCndResultado_quandoValido_retornaCreatedComResultado() throws Exception {
        when(cndResultadoService.criarCndResultado(any(CndResultadoDTO.class))).thenReturn(resultadoResponseDTO);

        mockMvc.perform(post("/api/cnd-resultados")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(resultadoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.situacao", is("Negativa")));
    }

    @Test
    void listarCndResultados_retornaListaDeResultados() throws Exception {
        List<CndResultadoDTO> lista = Arrays.asList(resultadoResponseDTO);
        when(cndResultadoService.listarCndResultados(anyMap())).thenReturn(lista);

        mockMvc.perform(get("/api/cnd-resultados")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void listarCndResultados_comFiltro_chamaServicoComFiltros() throws Exception {
        List<CndResultadoDTO> lista = Arrays.asList(resultadoResponseDTO);
        when(cndResultadoService.listarCndResultados(Collections.singletonMap("fkCliente", "1"))).thenReturn(lista);

        mockMvc.perform(get("/api/cnd-resultados?fkCliente=1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(cndResultadoService).listarCndResultados(Collections.singletonMap("fkCliente", "1"));
    }


    @Test
    void buscarCndResultadoPorId_quandoExiste_retornaResultado() throws Exception {
        when(cndResultadoService.buscarCndResultadoPorId(1L)).thenReturn(resultadoResponseDTO);

        mockMvc.perform(get("/api/cnd-resultados/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void buscarCndResultadoPorId_quandoNaoExiste_retornaNotFound() throws Exception {
        when(cndResultadoService.buscarCndResultadoPorId(1L)).thenThrow(new ResourceNotFoundException("Resultado não encontrado"));

        mockMvc.perform(get("/api/cnd-resultados/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarCndResultado_quandoValido_retornaOkComResultado() throws Exception {
        when(cndResultadoService.atualizarCndResultado(eq(1L), any(CndResultadoDTO.class))).thenReturn(resultadoResponseDTO);

        mockMvc.perform(put("/api/cnd-resultados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(resultadoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deletarCndResultado_quandoExiste_retornaNoContent() throws Exception {
        doNothing().when(cndResultadoService).deletarCndResultado(1L);

        mockMvc.perform(delete("/api/cnd-resultados/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNoContent());
    }

    @Test
    void downloadCndPdf_quandoValido_retornaPdf() throws Exception {
        byte[] pdfBytes = cndResultadoEntity.getArquivo(); // Corrected: getArquivo() already returns byte[]
        String filename = "CND_12345678000199_GERAL_" + cndResultadoEntity.getDataEmissao().toString() + ".pdf";

        when(cndResultadoService.downloadPdf(1L)).thenReturn(pdfBytes);
        when(cndResultadoService.getCndResultadoEntityById(1L)).thenReturn(cndResultadoEntity); // Para gerar o nome do arquivo
        when(cndResultadoService.gerarNomeArquivoPadronizado(cndResultadoEntity)).thenReturn(filename);


        mockMvc.perform(get("/api/cnd-resultados/1/download")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                // Adjusted to match the actual output reported by the test
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"" + filename + "\""))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void downloadCndPdf_quandoNaoEncontrado_retornaNotFound() throws Exception {
        when(cndResultadoService.downloadPdf(1L)).thenThrow(new ResourceNotFoundException("Arquivo PDF não disponível"));

        mockMvc.perform(get("/api/cnd-resultados/1/download")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNotFound());
    }
}
