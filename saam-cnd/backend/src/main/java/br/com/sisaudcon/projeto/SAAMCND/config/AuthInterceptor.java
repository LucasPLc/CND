package br.com.sisaudcon.projeto.SAAMCND.config;

import br.com.sisaudcon.projeto.SAAMCND.exception.ClienteNaoAutorizadoException;
import br.com.sisaudcon.projeto.SAAMCND.service.SaamCrValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private SaamCrValidationService saamCrValidationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();

        // Permitir acesso ao Swagger UI e API docs sem validação
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/api-docs") || requestUri.startsWith("/v3/api-docs")) {
            return true;
        }

        // Permitir acesso a Actuator endpoints (se configurado e desejado)
        if (requestUri.startsWith("/actuator")) {
            // Adicionar lógica de segurança específica para actuator se necessário
            return true;
        }

        // Extrair o IDCLIENTE do request. Pode ser de um header, parâmetro, ou parte da URL.
        // A PEC-4923 não especifica como o IDCLIENTE é passado para a API-CND.
        // Vamos assumir um header customizado "X-ID-CLIENTE" por enquanto.
        String idCliente = request.getHeader("X-ID-CLIENTE");

        if (idCliente == null || idCliente.trim().isEmpty()) {
            logger.warn("Tentativa de acesso sem X-ID-CLIENTE no header para URI: {}", requestUri);
            // A PEC-4923 especifica ClienteIdInvalidoException (400 Bad Request) para IDCLIENTE ausente/mal formatado.
            // Lançar uma exceção aqui que o GlobalExceptionHandler pode pegar.
            // Ou customizar a resposta diretamente.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"IDCLIENTE inválido ou não informado. Header 'X-ID-CLIENTE' ausente.\"}");
            return false;
        }

        logger.debug("Interceptando requisição para {}. Validando cliente com ID: {}", requestUri, idCliente);

        try {
            // A validação já lança ClienteNaoAutorizadoException ou ServicoExternoException
            // que são tratadas pelo GlobalExceptionHandler.
            boolean autorizado = saamCrValidationService.isClienteAutorizado(idCliente);

            if (autorizado) {
                logger.info("Cliente {} autorizado para acessar {}", idCliente, requestUri);
                return true; // Prossegue para o controller
            } else {
                // Este caso não deveria ser alcançado se isClienteAutorizado lançar exceção em caso de não autorizado.
                // Mas por segurança:
                logger.warn("Cliente {} não autorizado (retorno inesperado do serviço de validação) para URI: {}", idCliente, requestUri);
                throw new ClienteNaoAutorizadoException("Acesso negado. Cliente sem autorização ativa.");
            }
        } catch (ClienteNaoAutorizadoException e) {
            logger.warn("ClienteNaoAutorizadoException para cliente {} na URI {}: {}", idCliente, requestUri, e.getMessage());
            throw e; // Re-lança para ser pego pelo GlobalExceptionHandler
        } catch (Exception e) { // Captura ServicoExternoException e outros
            logger.error("Erro durante a validação do cliente {} para URI {}: {}", idCliente, requestUri, e.getMessage());
            throw e; // Re-lança para ser pego pelo GlobalExceptionHandler
        }
    }
}
