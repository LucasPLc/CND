package br.com.sisaudcon.projeto.SAAMCND.controller;

import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteRequestDTO;
import br.com.sisaudcon.projeto.SAAMCND.dto.CndClienteResponseDTO;
import br.com.sisaudcon.projeto.SAAMCND.model.CndEmpresa;
import br.com.sisaudcon.projeto.SAAMCND.service.CndClienteService;
import br.com.sisaudcon.projeto.SAAMCND.service.SaamCrValidationService;
import br.com.sisaudcon.projeto.SAAMCND.exception.ResourceNotFoundException;
import br.com.sisaudcon.projeto.SAAMCND.exception.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CndClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CndClienteService cndClienteService;

    @MockBean // Mockar para todos os testes de controller, pois o AuthInterceptor será acionado
    private SaamCrValidationService saamCrValidationService;


    @Autowired
    private ObjectMapper objectMapper;

    private CndClienteRequestDTO clienteRequestDTO;
    private CndClienteResponseDTO clienteResponseDTO;
    private CndEmpresa empresa;

    private final String VALID_ID_CLIENTE_HEADER = "CLIENTE_VALIDO_XYZ";

    @BeforeEach
    void setUp() {
        empresa = new CndEmpresa(1L, "00.000.000/0001-00", "Empresa Teste", "ATIVO_SAAM", null, null, LocalDateTime.now(), LocalDateTime.now());

        clienteRequestDTO = new CndClienteRequestDTO(
                "12.345.678/0001-99",
                30,
                "ATIVO",
                true,
                false,
                true,
                1L
        );

        clienteResponseDTO = new CndClienteResponseDTO(
                1L,
                "12.345.678/0001-99",
                30,
                "ATIVO",
                true,
                false,
                true,
                1L,
                "Empresa Teste",
                "INSERT-PEC-4924",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Configurar mock do SaamCrValidationService para permitir acesso
        // O AuthInterceptor vai chamar isso.
        when(saamCrValidationService.isClienteAutorizado(VALID_ID_CLIENTE_HEADER)).thenReturn(true);
    }

    @Test
    void criarCliente_quandoValido_retornaCreatedComCliente() throws Exception {
        when(cndClienteService.criarCliente(any(CndClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cnpj", is("12.345.678/0001-99")));
    }

    @Test
    void criarCliente_quandoInvalido_retornaBadRequest() throws Exception {
        CndClienteRequestDTO requestInvalido = new CndClienteRequestDTO(); // CNPJ nulo
        requestInvalido.setFkEmpresa(1L); // fkEmpresa é NotNull

        // O @Valid no controller vai pegar isso, não precisa mockar o serviço para lançar erro de validação
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("CNPJ do cliente é obrigatório")));
    }

    @Test
    void criarCliente_semHeaderIdCliente_retornaBadRequestPeloInterceptor() throws Exception {
        // Não configurar mock para saamCrValidationService.isClienteAutorizado sem header
        // O AuthInterceptor deve bloquear antes.
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isBadRequest()) // Esperado do AuthInterceptor
                .andExpect(jsonPath("$.error", containsString("IDCLIENTE inválido ou não informado")));
    }


    @Test
    void listarClientes_retornaListaDeClientes() throws Exception {
        List<CndClienteResponseDTO> lista = Arrays.asList(clienteResponseDTO);
        when(cndClienteService.listarClientes()).thenReturn(lista);

        mockMvc.perform(get("/api/clientes")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cnpj", is("12.345.678/0001-99")));
    }

    @Test
    void buscarClientePorId_quandoExiste_retornaCliente() throws Exception {
        when(cndClienteService.buscarClientePorId(1L)).thenReturn(clienteResponseDTO);

        mockMvc.perform(get("/api/clientes/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cnpj", is("12.345.678/0001-99")));
    }

    @Test
    void buscarClientePorId_quandoNaoExiste_retornaNotFound() throws Exception {
        when(cndClienteService.buscarClientePorId(1L)).thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/api/clientes/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));
    }

    @Test
    void atualizarCliente_quandoValido_retornaOkComCliente() throws Exception {
        when(cndClienteService.atualizarCliente(eq(1L), any(CndClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cnpj", is("12.345.678/0001-99")));
    }

    @Test
    void atualizarCliente_quandoNaoEncontrado_retornaNotFound() throws Exception {
        when(cndClienteService.atualizarCliente(eq(1L), any(CndClienteRequestDTO.class)))
            .thenThrow(new ResourceNotFoundException("Cliente não encontrado para o ID informado: 1"));

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER)
                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado para o ID informado: 1")));
    }


    @Test
    void deletarCliente_quandoExiste_retornaNoContent() throws Exception {
        doNothing().when(cndClienteService).deletarCliente(1L);

        mockMvc.perform(delete("/api/clientes/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarCliente_quandoNaoExiste_retornaNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Cliente não encontrado")).when(cndClienteService).deletarCliente(1L);

        mockMvc.perform(delete("/api/clientes/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente não encontrado")));
    }

    @Test
    void deletarCliente_quandoVinculadoAResultados_retornaBadRequest() throws Exception {
        doThrow(new BadRequestException("Não é possível excluir o cliente. Existem resultados de CND vinculados."))
            .when(cndClienteService).deletarCliente(1L);

        mockMvc.perform(delete("/api/clientes/1")
                .header("X-ID-CLIENTE", VALID_ID_CLIENTE_HEADER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Não é possível excluir o cliente. Existem resultados de CND vinculados.")));
    }
}
