package app.mendnook.hub.gathering;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workshop_gatherings")
public class WorkshopGathering {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String theme;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String venue;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Min(1)
    @Max(80)
    @Column(nullable = false)
    private int helperCapacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GatheringState state;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected WorkshopGathering() {
    }

    public WorkshopGathering(String theme, String venue, LocalDateTime startsAt, int helperCapacity) {
        this.theme = theme.trim();
        this.venue = venue.trim();
        this.startsAt = startsAt;
        this.helperCapacity = helperCapacity;
        this.state = GatheringState.PLANNED;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public String getVenue() {
        return venue;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public int getHelperCapacity() {
        return helperCapacity;
    }

    public GatheringState getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void revise(String theme, String venue, LocalDateTime startsAt, int helperCapacity) {
        this.theme = theme.trim();
        this.venue = venue.trim();
        this.startsAt = startsAt;
        this.helperCapacity = helperCapacity;
    }

    public void changeState(GatheringState state) {
        this.state = state;
    }
}
