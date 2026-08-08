package app.mendnook.hub.pledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompletionForm {

    @NotBlank(message = "Record the repair outcome")
    @Size(min = 10, max = 800, message = "Outcome must contain between 10 and 800 characters")
    private String outcomeNote;

    public String getOutcomeNote() {
        return outcomeNote;
    }

    public void setOutcomeNote(String outcomeNote) {
        this.outcomeNote = outcomeNote;
    }
}
