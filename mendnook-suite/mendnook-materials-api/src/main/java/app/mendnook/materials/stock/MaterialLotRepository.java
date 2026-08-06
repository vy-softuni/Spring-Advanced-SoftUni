package app.mendnook.materials.stock;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialLotRepository extends JpaRepository<MaterialLot, UUID> {

    List<MaterialLot> findAllByOrderByLabelAsc();

    List<MaterialLot> findAllByLabelContainingIgnoreCaseOrderByLabelAsc(String query);
}
