package app.mendnook.hub.mend;

import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.member.MemberAccountService;
import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.MissingRecordException;
import app.mendnook.hub.shared.TrackedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MendRequestService {

    private static final Logger log = LoggerFactory.getLogger(MendRequestService.class);

    private final MendRequestRepository repository;
    private final MemberAccountService memberAccountService;
    private final ApplicationEventPublisher eventPublisher;

    public MendRequestService(MendRequestRepository repository,
                              MemberAccountService memberAccountService,
                              ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.memberAccountService = memberAccountService;
        this.eventPublisher = eventPublisher;
    }

    @TrackedAction("mend-submission")
    @Transactional
    @PreAuthorize("hasAuthority('SUBMIT_MEND')")
    public MendRequest submit(MendRequestForm form, String ownerEmail) {
        MemberAccount owner = memberAccountService.findByEmail(ownerEmail);
        MendRequest request = new MendRequest(
                form.getItemLabel(), form.getItemKind(), form.getFaultStory(), form.getUrgency(), owner);
        MendRequest saved = repository.save(request);
        log.info("function=submit-mend mendRequestId={} ownerId={}", saved.getId(), owner.getId());
        eventPublisher.publishEvent(new MendSubmittedEvent(saved.getId(), owner.getEmail()));
        return saved;
    }

    @TrackedAction("mend-revision")
    @Transactional
    public void revise(UUID id, MendRequestForm form, String actorEmail) {
        MendRequest request = findOwned(id, actorEmail);
        requireState(request, MendState.SUBMITTED, "Only submitted requests can be revised");
        request.revise(form.getItemLabel(), form.getItemKind(), form.getFaultStory(), form.getUrgency());
        log.info("function=revise-mend mendRequestId={}", id);
    }

    @TrackedAction("mend-cancellation")
    @Transactional
    public void cancel(UUID id, String actorEmail) {
        MendRequest request = findOwned(id, actorEmail);
        if (request.getState() != MendState.SUBMITTED) {
            throw new DomainRuleException("Only an unassigned request can be cancelled");
        }
        request.cancel();
        log.info("function=cancel-mend mendRequestId={}", id);
    }

    @Transactional(readOnly = true)
    public MendRequest findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MissingRecordException("Repair request was not found"));
    }

    @Transactional(readOnly = true)
    public MendRequest findVisible(UUID id, String actorEmail, boolean privileged) {
        MendRequest request = findById(id);
        if (!privileged && !request.getOwner().getEmail().equalsIgnoreCase(actorEmail)) {
            throw new DomainRuleException("You cannot view another member's request");
        }
        return request;
    }

    @Transactional(readOnly = true)
    public List<MendRequest> findOwnedBy(String email) {
        MemberAccount member = memberAccountService.findByEmail(email);
        return repository.findAllByOwnerIdOrderBySubmittedAtDesc(member.getId());
    }

    @Transactional(readOnly = true)
    public List<MendRequest> findAvailableForPledging() {
        return repository.findAllByStateOrderByUrgencyDescSubmittedAtAsc(MendState.SUBMITTED);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public List<MendRequest> findAllForReport() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(MendRequest::getSubmittedAt).reversed())
                .toList();
    }

    @Transactional
    public int expireStaleSubmissions(Duration age) {
        Instant threshold = Instant.now().minus(age);
        List<MendRequest> staleRequests = repository.findAllByStateAndSubmittedAtBefore(MendState.SUBMITTED, threshold);
        staleRequests.forEach(MendRequest::expire);
        if (!staleRequests.isEmpty()) {
            log.info("job=expire-stale-mends affected={}", staleRequests.size());
        }
        return staleRequests.size();
    }

    private MendRequest findOwned(UUID id, String ownerEmail) {
        MendRequest request = findById(id);
        if (!request.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new DomainRuleException("You may change only your own repair requests");
        }
        return request;
    }

    private void requireState(MendRequest request, MendState expected, String message) {
        if (request.getState() != expected) {
            throw new DomainRuleException(message);
        }
    }
}
