package app.mendnook.hub.pledge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HelperPledgeRepository extends JpaRepository<HelperPledge, UUID> {

    List<HelperPledge> findAllByHelperIdOrderByPledgedAtDesc(UUID helperId);

    List<HelperPledge> findAllByStateOrderByPledgedAtAsc(PledgeState state);

    long countByGatheringIdAndState(UUID gatheringId, PledgeState state);

    boolean existsByHelperIdAndMendRequestIdAndGatheringId(UUID helperId, UUID mendRequestId, UUID gatheringId);

    boolean existsByMendRequestIdAndState(UUID mendRequestId, PledgeState state);
}
