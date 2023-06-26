package exceptions;

public class InvalidMaterialDataException extends RuntimeException {
    public InvalidMaterialDataException(String message) {
        super(message);
    }
}