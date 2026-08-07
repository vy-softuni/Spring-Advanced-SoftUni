package app.mendnook.hub.mend;

import app.mendnook.hub.member.MemberAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mend_requests")
public class MendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String itemLabel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemKind itemKind;

    @NotBlank
    @Size(min = 20, max = 1200)
    @Column(nullable = false, length = 1200)
    private String faultStory;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int urgency;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private MemberAccount owner;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private MendState state;

    @Size(max = 800)
    @Column(length = 800)
    private String outcomeNote;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(nullable = false)
    private Instant revisedAt;

    protected MendRequest() {
    }

    public MendRequest(String itemLabel, ItemKind itemKind, String faultStory, int urgency, MemberAccount owner) {
        this.itemLabel = itemLabel.trim();
        this.itemKind = itemKind;
        this.faultStory = faultStory.trim();
        this.urgency = urgency;
        this.owner = owner;
        this.state = MendState.SUBMITTED;
        this.submittedAt = Instant.now();
        this.revisedAt = this.submittedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getItemLabel() {
        return itemLabel;
    }

    public ItemKind getItemKind() {
        return itemKind;
    }

    public String getFaultStory() {
        return faultStory;
    }

    public int getUrgency() {
        return urgency;
    }

    public MemberAccount getOwner() {
        return owner;
    }

    public MendState getState() {
        return state;
    }

    public String getOutcomeNote() {
        return outcomeNote;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getRevisedAt() {
        return revisedAt;
    }

    public void revise(String itemLabel, ItemKind itemKind, String faultStory, int urgency) {
        this.itemLabel = itemLabel.trim();
        this.itemKind = itemKind;
        this.faultStory = faultStory.trim();
        this.urgency = urgency;
        this.revisedAt = Instant.now();
    }

    public void assign() {
        this.state = MendState.ASSIGNED;
        this.revisedAt = Instant.now();
    }

    public void complete(String outcomeNote) {
        this.state = MendState.COMPLETED;
        this.outcomeNote = outcomeNote.trim();
        this.revisedAt = Instant.now();
    }

    public void cancel() {
        this.state = MendState.CANCELLED;
        this.revisedAt = Instant.now();
    }

    public void expire() {
        this.state = MendState.EXPIRED;
        this.revisedAt = Instant.now();
    }
}
