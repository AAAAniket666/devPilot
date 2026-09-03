package devPilot.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedBackendException extends RuntimeException {
    public UnauthorizedBackendException(String message) {
        super(message);
    }  

}