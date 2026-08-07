package app.mendnook.hub.mend;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MendRequestForm {

    @NotBlank(message = "Name the item")
    @Size(max = 100, message = "Item name must be at most 100 characters")
    private String itemLabel;

    @NotNull(message = "Choose an item kind")
    private ItemKind itemKind;

    @NotBlank(message = "Describe the fault")
    @Size(min = 20, max = 1200, message = "Description must contain between 20 and 1200 characters")
    private String faultStory;

    @Min(value = 1, message = "Urgency must be between 1 and 5")
    @Max(value = 5, message = "Urgency must be between 1 and 5")
    private int urgency = 3;

    public static MendRequestForm from(MendRequest request) {
        MendRequestForm form = new MendRequestForm();
        form.setItemLabel(request.getItemLabel());
        form.setItemKind(request.getItemKind());
        form.setFaultStory(request.getFaultStory());
        form.setUrgency(request.getUrgency());
        return form;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public ItemKind getItemKind() {
        return itemKind;
    }

    public void setItemKind(ItemKind itemKind) {
        this.itemKind = itemKind;
    }

    public String getFaultStory() {
        return faultStory;
    }

    public void setFaultStory(String faultStory) {
        this.faultStory = faultStory;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }
}
