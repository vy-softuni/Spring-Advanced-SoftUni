package app.mendnook.materials.web;

import app.mendnook.materials.allocation.AllocationCreateRequest;
import app.mendnook.materials.allocation.MaterialAllocationService;
import app.mendnook.materials.allocation.MaterialAllocationView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/material-allocations")
public class MaterialAllocationController {

    private final MaterialAllocationService allocationService;

    public MaterialAllocationController(MaterialAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    List<MaterialAllocationView> findForMendRequest(@RequestParam UUID mendRequestId) {
        return allocationService.findForMendRequest(mendRequestId);
    }

    @PostMapping
    ResponseEntity<MaterialAllocationView> create(@Valid @RequestBody AllocationCreateRequest request) {
        MaterialAllocationView created = allocationService.create(request);
        return ResponseEntity.created(URI.create("/api/material-allocations/" + created.id())).body(created);
    }

    @PutMapping("/{id}/consume")
    MaterialAllocationView consume(@PathVariable UUID id) {
        return allocationService.consume(id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> release(@PathVariable UUID id) {
        allocationService.release(id);
        return ResponseEntity.noContent().build();
    }
}
