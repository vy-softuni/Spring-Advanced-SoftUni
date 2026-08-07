package app.mendnook.hub.member;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "member_accounts", uniqueConstraints = @UniqueConstraint(name = "uk_member_email", columnNames = "email"))
public class MemberAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Email
    @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String displayName;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String passwordHash;

    @Size(max = 500)
    @Column(length = 500)
    private String repairInterests;

    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, length = 30)
    private Set<AppRole> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    protected MemberAccount() {
    }

    public MemberAccount(String email, String displayName, String passwordHash, Set<AppRole> roles) {
        this.email = normalizeEmail(email);
        this.displayName = displayName.trim();
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>(roles);
        this.enabled = true;
        this.joinedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRepairInterests() {
        return repairInterests;
    }

    public Set<AppRole> getRoles() {
        return Set.copyOf(roles);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void reviseProfile(String displayName, String repairInterests) {
        this.displayName = displayName.trim();
        this.repairInterests = repairInterests == null ? null : repairInterests.trim();
    }

    public void replaceRoles(Set<AppRole> roles) {
        this.roles = new HashSet<>(roles);
    }

    public void changeEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
