package app.mendnook.hub.shared;

public class MissingRecordException extends RuntimeException {

    public MissingRecordException(String message) {
        super(message);
    }
}
