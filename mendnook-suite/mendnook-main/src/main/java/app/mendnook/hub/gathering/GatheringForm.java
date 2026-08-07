package app.mendnook.hub.gathering;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class GatheringForm {

    @NotBlank(message = "Enter a workshop theme")
    @Size(max = 120, message = "Theme must be at most 120 characters")
    private String theme;

    @NotBlank(message = "Enter a venue")
    @Size(max = 180, message = "Venue must be at most 180 characters")
    private String venue;

    @NotNull(message = "Choose a date and time")
    @Future(message = "The workshop must be scheduled in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startsAt;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 80, message = "Capacity must be at most 80")
    private int helperCapacity = 12;

    public static GatheringForm from(WorkshopGathering gathering) {
        GatheringForm form = new GatheringForm();
        form.setTheme(gathering.getTheme());
        form.setVenue(gathering.getVenue());
        form.setStartsAt(gathering.getStartsAt());
        form.setHelperCapacity(gathering.getHelperCapacity());
        return form;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public int getHelperCapacity() {
        return helperCapacity;
    }

    public void setHelperCapacity(int helperCapacity) {
        this.helperCapacity = helperCapacity;
    }
}
