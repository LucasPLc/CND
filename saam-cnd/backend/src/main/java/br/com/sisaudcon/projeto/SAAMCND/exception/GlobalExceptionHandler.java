package br.com.sisaudcon.projeto.SAAMCND.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Para @Valid errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        if (errors.isEmpty()) {
             errors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        }

        body.put("errors", errors);
        body.put("message", "Erro de validação");

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ClienteNaoAutorizadoException.class)
    public ResponseEntity<Object> handleClienteNaoAutorizadoException(
            ClienteNaoAutorizadoException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<Object> handleServicoExternoException(
            ServicoExternoException ex, WebRequest request) {
        // PEC-4923 especifica 503 para falha de integração com o robô de validação
        return buildErrorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflito de Dados");
        // Analisar a causa raiz para uma mensagem mais específica pode ser útil
        // ex.getCause() e ex.getMostSpecificCause()
        if (ex.getCause() != null && ex.getCause().getMessage().contains("violates unique constraint")) {
             body.put("message", "Violação de chave única. Um recurso com alguns desses identificadores já existe.");
        } else if (ex.getCause() != null && ex.getCause().getMessage().contains("violates foreign key constraint")) {
             body.put("message", "Violação de integridade referencial. Recurso relacionado não encontrado ou não pode ser deletado.");
        }
        else {
            body.put("message", "Erro de integridade dos dados: " + ex.getMostSpecificCause().getMessage());
        }
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class) // Handler genérico para outras exceções não tratadas
    public ResponseEntity<Object> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Erro inesperado: ", ex); // Logar a exceção completa
        return buildErrorResponse(ex, "Ocorreu um erro interno no servidor. Tente novamente mais tarde.", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception ex, HttpStatus status, WebRequest request) {
        return buildErrorResponse(ex, ex.getMessage(), status, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception ex, String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
