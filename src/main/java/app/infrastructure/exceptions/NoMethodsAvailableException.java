package app.infrastructure.exceptions;

public class NoMethodsAvailableException extends RuntimeException {
    private String details;

    public NoMethodsAvailableException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
