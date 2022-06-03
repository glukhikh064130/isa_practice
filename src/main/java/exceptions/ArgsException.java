package exceptions;

public class ArgsException extends GenericException {
    private static final int CODE = 1;
    private static final String MSG = "Incorrect CLI arguments";

    public ArgsException(String details) {
        super(CODE, MSG, details);
    }
}
