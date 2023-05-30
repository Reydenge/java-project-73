package hexlet.code.exception;

import jakarta.persistence.EntityNotFoundException;

public class LabelNotFoundException extends EntityNotFoundException {
    public LabelNotFoundException(String message) {
        super(message);
    }
}
