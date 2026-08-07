package app.mendnook.hub.mend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MendRequestRepository extends JpaRepository<MendRequest, UUID> {

    List<MendRequest> findAllByOwnerIdOrderBySubmittedAtDesc(UUID ownerId);

    List<MendRequest> findAllByStateOrderByUrgencyDescSubmittedAtAsc(MendState state);

    List<MendRequest> findAllByStateAndSubmittedAtBefore(MendState state, Instant threshold);
}
