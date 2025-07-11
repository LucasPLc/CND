package br.com.sisaudcon.projeto.SAAMCND.service;

import br.com.sisaudcon.projeto.SAAMCND.exception.ClienteNaoAutorizadoException;
import br.com.sisaudcon.projeto.SAAMCND.exception.ServicoExternoException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SaamCrValidationService {

    private static final Logger logger = LoggerFactory.getLogger(SaamCrValidationService.class);

    private final RestTemplate restTemplate;
    private final String saamCrValidationUrl;
    private final ObjectMapper objectMapper;

    public SaamCrValidationService(RestTemplateBuilder restTemplateBuilder,
                                   @Value("${saam.cr.validation.url}") String saamCrValidationUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.saamCrValidationUrl = saamCrValidationUrl;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Valida se um cliente está autorizado a acessar o sistema, consultando o SAAM-CR.
     *
     * @param idCliente O ID do cliente a ser validado.
     * @return true se autorizado, false caso contrário.
     * @throws ClienteNaoAutorizadoException se o cliente não está autorizado (situação diferente de "1").
     * @throws ServicoExternoException se houver falha na comunicação com o SAAM-CR ou resposta inesperada.
     */
    public boolean isClienteAutorizado(String idCliente) {
        if (idCliente == null || idCliente.trim().isEmpty()) {
            // A PEC-4923 menciona ClienteIdInvalidoException com 400 Bad Request.
            // Isso pode ser lançado antes de chamar este serviço ou tratado aqui.
            // Por ora, o filtro/interceptor que chama este método deve garantir um ID válido.
            logger.warn("ID do Cliente para validação SAAM-CR está vazio ou nulo.");
            throw new ServicoExternoException("IDCLIENTE inválido ou não informado para validação no SAAM-CR.");
        }

        String finalUrl = saamCrValidationUrl.replace("{IDCLIENTE}", idCliente);

        logger.debug("Validando cliente ID {} no SAAM-CR: URL {}", idCliente, finalUrl);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(finalUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String responseBody = response.getBody();
                logger.debug("Resposta do SAAM-CR para cliente {}: {}", idCliente, responseBody);

                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode situacaoNode = rootNode.path("situacao");

                if (situacaoNode.isMissingNode()) {
                    logger.error("Campo 'situacao' não encontrado na resposta do SAAM-CR para o cliente {}. Resposta: {}", idCliente, responseBody);
                    throw new ServicoExternoException("Resposta inválida do serviço de validação SAAM-CR: campo 'situacao' ausente.");
                }

                String situacaoValor = situacaoNode.asText();
                if ("1".equals(situacaoValor)) {
                    logger.info("Cliente {} autorizado pelo SAAM-CR (situação: {})", idCliente, situacaoValor);
                    return true;
                } else {
                    logger.warn("Cliente {} NÃO autorizado pelo SAAM-CR (situação: {}).", idCliente, situacaoValor);
                    throw new ClienteNaoAutorizadoException("Acesso negado. Cliente sem autorização ativa no SAAM-CR (Situação: " + situacaoValor + ")");
                }
            } else {
                logger.error("Erro ao validar cliente {} no SAAM-CR. Status: {}, Body: {}", idCliente, response.getStatusCode(), response.getBody());
                throw new ServicoExternoException("Serviço de validação SAAM-CR retornou status inesperado: " + response.getStatusCode());
            }
        } catch (ClienteNaoAutorizadoException e) { // Deixar ClienteNaoAutorizadoException propagar
            throw e;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Erro HTTP ao conectar com SAAM-CR para cliente {}: {} - {}", idCliente, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new ServicoExternoException("Serviço de validação SAAM-CR indisponível ou retornou erro: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            logger.error("Erro de acesso (timeout, DNS) ao conectar com SAAM-CR para cliente {}: {}", idCliente, e.getMessage(), e);
            throw new ServicoExternoException("Falha na comunicação com o serviço de validação SAAM-CR (timeout ou problema de rede).", e);
        } catch (IOException e) { // JsonProcessingException é uma IOException
            if (e instanceof com.fasterxml.jackson.core.JsonProcessingException) {
                logger.error("Erro ao parsear JSON de resposta do SAAM-CR para cliente {}: {}", idCliente, e.getMessage(), e);
                throw new ServicoExternoException("Resposta inválida (JSON malformado) do serviço de validação SAAM-CR.", e);
            } else {
                logger.error("Erro de IO ao comunicar com SAAM-CR para cliente {}: {}", idCliente, e.getMessage(), e);
                throw new ServicoExternoException("Erro de IO ao processar resposta do serviço de validação SAAM-CR.", e);
            }
        } catch (Exception e) {
            logger.error("Erro inesperado durante validação do cliente {} no SAAM-CR: {}", idCliente, e.getMessage(), e);
            throw new ServicoExternoException("Erro inesperado ao contatar o serviço de validação SAAM-CR.", e);
        }
    }
}
