package app.mendnook.materials.allocation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialAllocationRepository extends JpaRepository<MaterialAllocation, UUID> {

    List<MaterialAllocation> findAllByMendRequestIdOrderByCreatedAtDesc(UUID mendRequestId);
}
