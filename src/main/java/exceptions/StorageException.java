package exceptions;

public class StorageException extends GenericException {
    private static final int CODE = 2;
    private static final String MSG = "Data storage error";

    public StorageException(String details, Throwable cause) {
        super(CODE, MSG, details, cause);
    }
}
