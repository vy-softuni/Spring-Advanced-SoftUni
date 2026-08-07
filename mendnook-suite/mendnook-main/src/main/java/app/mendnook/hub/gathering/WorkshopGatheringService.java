package app.mendnook.hub.gathering;

import app.mendnook.hub.shared.DomainRuleException;
import app.mendnook.hub.shared.MissingRecordException;
import app.mendnook.hub.shared.TrackedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WorkshopGatheringService {

    private static final Logger log = LoggerFactory.getLogger(WorkshopGatheringService.class);

    private final WorkshopGatheringRepository repository;

    public WorkshopGatheringService(WorkshopGatheringRepository repository) {
        this.repository = repository;
    }

    @TrackedAction("gathering-creation")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZE_GATHERING')")
    @CacheEvict(value = "openGatherings", allEntries = true)
    public WorkshopGathering create(GatheringForm form) {
        WorkshopGathering gathering = new WorkshopGathering(
                form.getTheme(), form.getVenue(), form.getStartsAt(), form.getHelperCapacity());
        WorkshopGathering saved = repository.save(gathering);
        log.info("function=create-gathering gatheringId={}", saved.getId());
        return saved;
    }

    @TrackedAction("gathering-revision")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZE_GATHERING')")
    @CacheEvict(value = "openGatherings", allEntries = true)
    public void revise(UUID id, GatheringForm form) {
        WorkshopGathering gathering = findById(id);
        if (gathering.getState() == GatheringState.FINISHED || gathering.getState() == GatheringState.CANCELLED) {
            throw new DomainRuleException("A closed gathering cannot be revised");
        }
        gathering.revise(form.getTheme(), form.getVenue(), form.getStartsAt(), form.getHelperCapacity());
        log.info("function=revise-gathering gatheringId={}", id);
    }

    @TrackedAction("gathering-state-change")
    @Transactional
    @PreAuthorize("hasAuthority('ORGANIZE_GATHERING')")
    @CacheEvict(value = "openGatherings", allEntries = true)
    public void changeState(UUID id, GatheringState targetState) {
        WorkshopGathering gathering = findById(id);
        validateTransition(gathering.getState(), targetState);
        gathering.changeState(targetState);
        log.info("function=change-gathering-state gatheringId={} state={}", id, targetState);
    }

    @Transactional(readOnly = true)
    public WorkshopGathering findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MissingRecordException("Workshop gathering was not found"));
    }

    @Cacheable("openGatherings")
    @Transactional(readOnly = true)
    public List<WorkshopGathering> findPublicGatherings() {
        return repository.findAllByStateInOrderByStartsAtAsc(List.of(GatheringState.PLANNED, GatheringState.OPEN));
    }

    @Transactional(readOnly = true)
    public List<WorkshopGathering> findOpenGatherings() {
        return repository.findAllByStateOrderByStartsAtAsc(GatheringState.OPEN);
    }

    @Transactional(readOnly = true)
    public List<WorkshopGathering> findAll() {
        return repository.findAll().stream()
                .sorted((left, right) -> right.getStartsAt().compareTo(left.getStartsAt()))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "openGatherings", allEntries = true)
    public int finishPastGatherings() {
        List<WorkshopGathering> past = repository.findAllByStateAndStartsAtBefore(
                GatheringState.OPEN, LocalDateTime.now().minusHours(3));
        past.forEach(gathering -> gathering.changeState(GatheringState.FINISHED));
        if (!past.isEmpty()) {
            log.info("job=finish-past-gatherings affected={}", past.size());
        }
        return past.size();
    }

    private void validateTransition(GatheringState current, GatheringState target) {
        boolean valid = switch (current) {
            case PLANNED -> target == GatheringState.OPEN || target == GatheringState.CANCELLED;
            case OPEN -> target == GatheringState.FINISHED || target == GatheringState.CANCELLED;
            case FINISHED, CANCELLED -> false;
        };
        if (!valid) {
            throw new DomainRuleException("The requested gathering state change is not allowed");
        }
    }
}
