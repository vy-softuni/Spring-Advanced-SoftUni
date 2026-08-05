package app.mendnook.materials.shared;

public class MaterialMissingException extends RuntimeException {

    public MaterialMissingException(String message) {
        super(message);
    }
}
