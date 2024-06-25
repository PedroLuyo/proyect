package app.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
    private String details;

    public CustomException(String message, String details) {
        super(message);
        this.details = details;
    }


}
