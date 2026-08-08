package app.mendnook.hub.pledge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class PledgeForm {

    @NotNull(message = "Choose a workshop gathering")
    private UUID gatheringId;

    @NotBlank(message = "Describe how you can help")
    @Size(min = 10, max = 500, message = "Contribution note must contain between 10 and 500 characters")
    private String contributionNote;

    public UUID getGatheringId() {
        return gatheringId;
    }

    public void setGatheringId(UUID gatheringId) {
        this.gatheringId = gatheringId;
    }

    public String getContributionNote() {
        return contributionNote;
    }

    public void setContributionNote(String contributionNote) {
        this.contributionNote = contributionNote;
    }
}
