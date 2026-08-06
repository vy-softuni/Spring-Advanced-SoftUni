package app.mendnook.materials.stock;

import app.mendnook.materials.shared.MaterialMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialLotService {

    private static final Logger log = LoggerFactory.getLogger(MaterialLotService.class);

    private final MaterialLotRepository repository;

    public MaterialLotService(MaterialLotRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "materialLots", key = "#query == null ? '' : #query.toLowerCase()")
    @Transactional(readOnly = true)
    public List<MaterialLotView> search(String query) {
        List<MaterialLot> lots = query == null || query.isBlank()
                ? repository.findAllByOrderByLabelAsc()
                : repository.findAllByLabelContainingIgnoreCaseOrderByLabelAsc(query.trim());
        return lots.stream().map(MaterialLotView::from).toList();
    }

    @Transactional(readOnly = true)
    public MaterialLot findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MaterialMissingException("Material lot was not found"));
    }

    @Transactional
    @CacheEvict(value = "materialLots", allEntries = true)
    public MaterialLot hold(UUID id, int units) {
        MaterialLot lot = findById(id);
        lot.hold(units);
        return lot;
    }

    @Transactional
    @CacheEvict(value = "materialLots", allEntries = true)
    public void release(MaterialLot lot, int units) {
        lot.release(units);
    }

    @Transactional
    @CacheEvict(value = "materialLots", allEntries = true)
    public void consume(MaterialLot lot, int units) {
        lot.consume(units);
    }

    @Transactional
    @CacheEvict(value = "materialLots", allEntries = true)
    public MaterialLotView replaceStock(UUID id, int totalUnits) {
        MaterialLot lot = findById(id);
        lot.replaceTotalUnits(totalUnits);
        log.info("function=replace-stock materialId={} totalUnits={}", id, totalUnits);
        return MaterialLotView.from(lot);
    }
}
