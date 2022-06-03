package exceptions;

public class GenericException extends Exception {
    private final int code;
    private final String details;

    public GenericException(int code, String message, String details) {
        this(code, message, details, null);
    }

    public GenericException(int code, String message, String details, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }

    public boolean hasDetails() {
        return this.details != null && !this.details.equals("");
    }

    public boolean hasCause() {
        return this.getCause() != null;
    }

    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("[%s] ", this.getCode()));
        sb.append(this.getMessage());
        if (this.hasDetails()) {
            sb.append(String.format(": %s", this.getDetails()));
        }
        if (this.hasCause()) {
            sb.append(String.format("%n%s", this.getCause().toString()));
        }

        return sb.toString();
    }
}
