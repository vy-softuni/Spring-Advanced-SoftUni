package app.mendnook.hub.pledge;

import app.mendnook.hub.gathering.WorkshopGathering;
import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.mend.MendRequest;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "helper_pledges", uniqueConstraints = @UniqueConstraint(
        name = "uk_pledge_helper_mend_gathering",
        columnNames = {"helper_id", "mend_request_id", "gathering_id"}
))
public class HelperPledge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mend_request_id", nullable = false)
    private MendRequest mendRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gathering_id", nullable = false)
    private WorkshopGathering gathering;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "helper_id", nullable = false)
    private MemberAccount helper;

    @NotBlank
    @Size(min = 10, max = 500)
    @Column(nullable = false, length = 500)
    private String contributionNote;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PledgeState state;

    @Column(nullable = false, updatable = false)
    private Instant pledgedAt;

    protected HelperPledge() {
    }

    public HelperPledge(MendRequest mendRequest, WorkshopGathering gathering,
                        MemberAccount helper, String contributionNote) {
        this.mendRequest = mendRequest;
        this.gathering = gathering;
        this.helper = helper;
        this.contributionNote = contributionNote.trim();
        this.state = PledgeState.PENDING;
        this.pledgedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public MendRequest getMendRequest() {
        return mendRequest;
    }

    public WorkshopGathering getGathering() {
        return gathering;
    }

    public MemberAccount getHelper() {
        return helper;
    }

    public String getContributionNote() {
        return contributionNote;
    }

    public PledgeState getState() {
        return state;
    }

    public Instant getPledgedAt() {
        return pledgedAt;
    }

    public void changeState(PledgeState state) {
        this.state = state;
    }
}
