package br.com.sisaudcon.projeto.SAAMCND.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Usada para erros de comunicação com SAAM-CR ou outros serviços externos
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // Ou BAD_GATEWAY dependendo do contexto
public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException(String message) {
        super(message);
    }

    public ServicoExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
