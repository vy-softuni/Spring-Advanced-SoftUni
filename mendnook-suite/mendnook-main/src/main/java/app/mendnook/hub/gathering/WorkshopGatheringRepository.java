package app.mendnook.hub.gathering;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkshopGatheringRepository extends JpaRepository<WorkshopGathering, UUID> {

    List<WorkshopGathering> findAllByStateOrderByStartsAtAsc(GatheringState state);

    List<WorkshopGathering> findAllByStateInOrderByStartsAtAsc(List<GatheringState> states);

    List<WorkshopGathering> findAllByStateAndStartsAtBefore(GatheringState state, LocalDateTime threshold);
}
