package br.com.sisaudcon.projeto.SAAMCND.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ClienteNaoAutorizadoException extends RuntimeException {
    public ClienteNaoAutorizadoException(String message) {
        super(message);
    }
}
