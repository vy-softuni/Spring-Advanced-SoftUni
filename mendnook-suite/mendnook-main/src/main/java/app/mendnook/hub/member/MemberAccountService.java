package app.mendnook.hub.member;

import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.MissingRecordException;
import app.mendnook.hub.shared.TrackedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MemberAccountService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MemberAccountService.class);

    private final MemberAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public MemberAccountService(MemberAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberAccount account = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("No member uses that email address"));
        Set<GrantedAuthority> authorities = new HashSet<>();
        account.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
            role.getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.name()))
                    .forEach(authorities::add);
        });
        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .disabled(!account.isEnabled())
                .authorities(authorities)
                .build();
    }

    @TrackedAction("member-registration")
    @Transactional
    public MemberAccount register(RegistrationForm form) {
        if (repository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new DomainRuleException("An account already uses this email address");
        }
        MemberAccount account = new MemberAccount(
                form.getEmail(),
                form.getDisplayName(),
                passwordEncoder.encode(form.getPassword()),
                Set.of(AppRole.MEMBER)
        );
        MemberAccount saved = repository.save(account);
        log.info("function=register-member memberId={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public MemberAccount findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new MissingRecordException("Member account was not found"));
    }

    @Transactional(readOnly = true)
    public MemberAccount findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MissingRecordException("Member account was not found"));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<MemberAccount> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(MemberAccount::getDisplayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @TrackedAction("profile-revision")
    @Transactional
    public void reviseOwnProfile(String email, ProfileForm form) {
        MemberAccount account = findByEmail(email);
        account.reviseProfile(form.getDisplayName(), form.getRepairInterests());
        log.info("function=revise-profile memberId={}", account.getId());
    }

    @TrackedAction("member-role-change")
    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_MEMBERS')")
    public void replaceRoles(UUID memberId, Set<AppRole> roles, String administratorEmail) {
        if (roles == null || roles.isEmpty()) {
            throw new DomainRuleException("A member must retain at least one role");
        }
        MemberAccount target = findById(memberId);
        if (target.getEmail().equalsIgnoreCase(administratorEmail) && !roles.contains(AppRole.ADMIN)) {
            throw new DomainRuleException("You cannot remove your own administrator role");
        }
        target.replaceRoles(roles);
        log.info("function=replace-member-roles memberId={} roles={}", memberId, roles);
    }

    @TrackedAction("member-access-change")
    @Transactional
    @PreAuthorize("hasAuthority('MANAGE_MEMBERS')")
    public void changeEnabled(UUID memberId, boolean enabled, String administratorEmail) {
        MemberAccount target = findById(memberId);
        if (target.getEmail().equalsIgnoreCase(administratorEmail) && !enabled) {
            throw new DomainRuleException("You cannot disable your own account");
        }
        target.changeEnabled(enabled);
        log.info("function=change-member-access memberId={} enabled={}", memberId, enabled);
    }
}
