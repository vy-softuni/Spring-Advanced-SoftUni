package app.mendnook.hub.pledge;

import app.mendnook.hub.gathering.GatheringState;
import app.mendnook.hub.gathering.WorkshopGathering;
import app.mendnook.hub.gathering.WorkshopGatheringService;
import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.member.MemberAccountService;
import app.mendnook.hub.mend.MendRequest;
import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.mend.MendState;
import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.MissingRecordException;
import app.mendnook.hub.shared.TrackedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HelperPledgeService {

    private static final Logger log = LoggerFactory.getLogger(HelperPledgeService.class);

    private final HelperPledgeRepository repository;
    private final MendRequestService mendRequestService;
    private final WorkshopGatheringService gatheringService;
    private final MemberAccountService memberAccountService;
    private final ApplicationEventPublisher eventPublisher;

    public HelperPledgeService(HelperPledgeRepository repository,
                               MendRequestService mendRequestService,
                               WorkshopGatheringService gatheringService,
                               MemberAccountService memberAccountService,
                               ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.mendRequestService = mendRequestService;
        this.gatheringService = gatheringService;
        this.memberAccountService = memberAccountService;
        this.eventPublisher = eventPublisher;
    }

    @TrackedAction("helper-pledge")
    @Transactional
    @PreAuthorize("hasAuthority('PLEDGE_HELP')")
    public HelperPledge pledge(UUID mendRequestId, PledgeForm form, String helperEmail) {
        MemberAccount helper = memberAccountService.findByEmail(helperEmail);
        MendRequest mendRequest = mendRequestService.findById(mendRequestId);
        WorkshopGathering gathering = gatheringService.findById(form.getGatheringId());
        if (mendRequest.getState() != MendState.SUBMITTED) {
            throw new DomainRuleException("Help can be pledged only for an unassigned request");
        }
        if (gathering.getState() != GatheringState.OPEN) {
            throw new DomainRuleException("Choose a gathering that is open for helpers");
        }
        if (mendRequest.getOwner().getId().equals(helper.getId())) {
            throw new DomainRuleException("You cannot volunteer for your own repair request");
        }
        if (repository.existsByHelperIdAndMendRequestIdAndGatheringId(
                helper.getId(), mendRequestId, gathering.getId())) {
            throw new DomainRuleException("You have already pledged for this repair at that gathering");
        }
        HelperPledge saved = repository.save(new HelperPledge(
                mendRequest, gathering, helper, form.getContributionNote()));
        log.info("function=create-pledge pledgeId={} mendRequestId={}", saved.getId(), mendRequestId);
        return saved;
    }

    @TrackedAction("pledge-decision")
    @Transactional
    @PreAuthorize("hasAuthority('REVIEW_PLEDGE')")
    public void decide(UUID pledgeId, boolean accept) {
        HelperPledge pledge = findById(pledgeId);
        if (pledge.getState() != PledgeState.PENDING) {
            throw new DomainRuleException("Only a pending pledge can be reviewed");
        }
        if (accept) {
            ensureAcceptanceCapacity(pledge);
            pledge.changeState(PledgeState.ACCEPTED);
            pledge.getMendRequest().assign();
            eventPublisher.publishEvent(new PledgeAcceptedEvent(pledge.getId(), pledge.getMendRequest().getId()));
        } else {
            pledge.changeState(PledgeState.DECLINED);
        }
        log.info("function=decide-pledge pledgeId={} accepted={}", pledgeId, accept);
    }

    @TrackedAction("repair-completion")
    @Transactional
    public void complete(UUID pledgeId, CompletionForm form, String helperEmail) {
        HelperPledge pledge = findById(pledgeId);
        if (!pledge.getHelper().getEmail().equalsIgnoreCase(helperEmail)) {
            throw new DomainRuleException("Only the assigned helper can complete this repair");
        }
        if (pledge.getState() != PledgeState.ACCEPTED) {
            throw new DomainRuleException("Only an accepted pledge can be completed");
        }
        pledge.changeState(PledgeState.COMPLETED);
        pledge.getMendRequest().complete(form.getOutcomeNote());
        log.info("function=complete-repair pledgeId={} mendRequestId={}",
                pledgeId, pledge.getMendRequest().getId());
    }

    @TrackedAction("pledge-withdrawal")
    @Transactional
    public void withdraw(UUID pledgeId, String helperEmail) {
        HelperPledge pledge = findById(pledgeId);
        if (!pledge.getHelper().getEmail().equalsIgnoreCase(helperEmail)) {
            throw new DomainRuleException("Only the helper who made the pledge can withdraw it");
        }
        if (pledge.getState() != PledgeState.PENDING) {
            throw new DomainRuleException("Only a pending pledge can be withdrawn");
        }
        pledge.changeState(PledgeState.WITHDRAWN);
        log.info("function=withdraw-pledge pledgeId={}", pledgeId);
    }

    @Transactional(readOnly = true)
    public HelperPledge findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MissingRecordException("Helper pledge was not found"));
    }

    @Transactional(readOnly = true)
    public List<HelperPledge> findForHelper(String helperEmail) {
        MemberAccount helper = memberAccountService.findByEmail(helperEmail);
        return repository.findAllByHelperIdOrderByPledgedAtDesc(helper.getId());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('REVIEW_PLEDGE')")
    public List<HelperPledge> findPending() {
        return repository.findAllByStateOrderByPledgedAtAsc(PledgeState.PENDING);
    }

    private void ensureAcceptanceCapacity(HelperPledge pledge) {
        if (pledge.getMendRequest().getState() != MendState.SUBMITTED
                || repository.existsByMendRequestIdAndState(pledge.getMendRequest().getId(), PledgeState.ACCEPTED)) {
            throw new DomainRuleException("This repair request already has an assigned helper");
        }
        long accepted = repository.countByGatheringIdAndState(
                pledge.getGathering().getId(), PledgeState.ACCEPTED);
        if (accepted >= pledge.getGathering().getHelperCapacity()) {
            throw new DomainRuleException("The gathering has reached its helper capacity");
        }
    }
}
