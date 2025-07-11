package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.exception.ClienteNaoAutorizadoException;
import br.com.sisaudcon.projeto.SAAMCND.exception.ServicoExternoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException; // Import adicionado
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock; // Import adicionado

@ExtendWith(MockitoExtension.class)
class SaamCrValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy // Usar Spy para ObjectMapper real, se necessário, ou apenas instanciar.
    private ObjectMapper objectMapper = new ObjectMapper();

    // @Value("${saam.cr.validation.url}") não funciona bem em testes unitários puros.
    // Injete o valor diretamente no construtor do serviço ou use @TestPropertySource na classe de teste.
    private String baseUrl = "http://mock-saamcr.com/api/empresa/getAttributeById/GLSAAM?attribute=situacao";


    private SaamCrValidationService saamCrValidationServiceWithMockEnabled;
    private SaamCrValidationService saamCrValidationServiceWithMockDisabled;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder mockRestTemplateBuilder = mock(RestTemplateBuilder.class);
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);

        // Serviço com mock habilitado
        saamCrValidationServiceWithMockEnabled = new SaamCrValidationService(mockRestTemplateBuilder, baseUrl, true);

        // Serviço com mock desabilitado (para testar chamadas reais mockadas com RestTemplate)
        saamCrValidationServiceWithMockDisabled = new SaamCrValidationService(mockRestTemplateBuilder, baseUrl, false);
    }

    private String createJsonResponse(String situacao) throws JsonProcessingException {
        return objectMapper.writeValueAsString(Map.of("situacao", situacao));
    }

    // --- Testes com Mock Habilitado ---
    @Test
    void isClienteAutorizado_withMockEnabled_cliente1Autorizado() {
        assertTrue(saamCrValidationServiceWithMockEnabled.isClienteAutorizado("1"));
    }

    @Test
    void isClienteAutorizado_withMockEnabled_cliente2NaoAutorizado() {
        ClienteNaoAutorizadoException exception = assertThrows(ClienteNaoAutorizadoException.class,
                () -> saamCrValidationServiceWithMockEnabled.isClienteAutorizado("2"));
        assertTrue(exception.getMessage().contains("Situação: MOCK_0"));
    }

    @Test
    void isClienteAutorizado_withMockEnabled_cliente3ErroServico() {
        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockEnabled.isClienteAutorizado("3"));
        assertEquals("MOCK: Falha na comunicação com o serviço de validação SAAM-CR.", exception.getMessage());
    }

    @Test
    void isClienteAutorizado_withMockEnabled_clienteOutroAutorizado() {
        assertTrue(saamCrValidationServiceWithMockEnabled.isClienteAutorizado("99"));
    }

    // --- Testes com Mock Desabilitado (testando a lógica de chamada real) ---
    @Test
    void isClienteAutorizado_withMockDisabled_quandoSituacaoEh1_retornaTrue() throws JsonProcessingException {
        String idCliente = "VALIDO123";
        // A URL base é "http://mock-saamcr.com/api/empresa/getAttributeById/GLSAAM?attribute=situacao"
        // Esperamos que "?IDCLIENTE=VALIDO123" seja adicionado
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;
        ResponseEntity<String> mockResponse = new ResponseEntity<>(createJsonResponse("1"), HttpStatus.OK);

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class))).thenReturn(mockResponse);
        assertTrue(saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoSituacaoNaoEh1_lancaClienteNaoAutorizadoException() throws JsonProcessingException {
        String idCliente = "NAOAUTORIZADO";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;
        ResponseEntity<String> mockResponse = new ResponseEntity<>(createJsonResponse("0"), HttpStatus.OK);

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class))).thenReturn(mockResponse);

        ClienteNaoAutorizadoException exception = assertThrows(ClienteNaoAutorizadoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertTrue(exception.getMessage().contains("Acesso negado. Cliente sem autorização ativa no SAAM-CR (Situação: 0)"));
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoRespostaJsonSemCampoSituacao_lancaServicoExternoException() throws JsonProcessingException {
        String idCliente = "JSONINVALIDO";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;
        ResponseEntity<String> mockResponse = new ResponseEntity<>(objectMapper.writeValueAsString(Map.of("outroCampo", "valor")), HttpStatus.OK);

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class))).thenReturn(mockResponse);

        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertEquals("Resposta inválida do serviço de validação SAAM-CR: campo 'situacao' ausente.", exception.getMessage());
    }

    @Test
    void isClienteAutorizado_quandoIdClienteNuloOuVazio_lancaServicoExternoException() {
         ServicoExternoException exceptionNull = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(null)); // Pode ser com mock enabled ou disabled
        assertEquals("IDCLIENTE inválido ou não informado para validação no SAAM-CR.", exceptionNull.getMessage());

        ServicoExternoException exceptionEmpty = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado("  "));
        assertEquals("IDCLIENTE inválido ou não informado para validação no SAAM-CR.", exceptionEmpty.getMessage());
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoHttpErrorCliente_lancaServicoExternoException() {
        String idCliente = "ERRO4XX";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Erro do cliente"));

        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertTrue(exception.getMessage().contains("Serviço de validação SAAM-CR indisponível ou retornou erro: 400"));
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoHttpErrorServidor_lancaServicoExternoException() {
        String idCliente = "ERRO5XX";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro do servidor"));

        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertTrue(exception.getMessage().contains("Serviço de validação SAAM-CR indisponível ou retornou erro: 500"));
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoResourceAccessException_lancaServicoExternoException() {
        String idCliente = "TIMEOUT";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class)))
                .thenThrow(new ResourceAccessException("Timeout simulado"));

        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertTrue(exception.getMessage().contains("Falha na comunicação com o serviço de validação SAAM-CR (timeout ou problema de rede)."));
    }

    @Test
    void isClienteAutorizado_withMockDisabled_quandoJsonInvalidoRetornado_lancaServicoExternoException() {
        String idCliente = "BADJSON";
        String expectedUrl = baseUrl + "&IDCLIENTE=" + idCliente;
        ResponseEntity<String> mockResponse = new ResponseEntity<>("IstoNaoEhJSON", HttpStatus.OK);

        when(restTemplate.getForEntity(eq(URI.create(expectedUrl)), eq(String.class))).thenReturn(mockResponse);

        ServicoExternoException exception = assertThrows(ServicoExternoException.class,
                () -> saamCrValidationServiceWithMockDisabled.isClienteAutorizado(idCliente));
        assertTrue(exception.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException);
        assertEquals("Resposta inválida (JSON malformado) do serviço de validação SAAM-CR.", exception.getMessage());
    }
}
