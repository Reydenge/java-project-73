package hexlet.code.exception;

import jakarta.persistence.EntityNotFoundException;

public class TaskStatusNotFoundException extends EntityNotFoundException {
    public TaskStatusNotFoundException(String message) {
        super(message);
    }
}
