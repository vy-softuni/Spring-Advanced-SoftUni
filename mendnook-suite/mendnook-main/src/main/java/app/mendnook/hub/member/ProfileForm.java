package app.mendnook.hub.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileForm {

    @NotBlank(message = "Enter your name")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String displayName;

    @Size(max = 500, message = "Interests must be at most 500 characters")
    private String repairInterests;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRepairInterests() {
        return repairInterests;
    }

    public void setRepairInterests(String repairInterests) {
        this.repairInterests = repairInterests;
    }
}
