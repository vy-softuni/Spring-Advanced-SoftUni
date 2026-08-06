package app.mendnook.materials.web;

import app.mendnook.materials.stock.MaterialLotService;
import app.mendnook.materials.stock.MaterialLotView;
import app.mendnook.materials.stock.StockLevelRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/material-lots")
public class MaterialLotController {

    private final MaterialLotService materialLotService;

    public MaterialLotController(MaterialLotService materialLotService) {
        this.materialLotService = materialLotService;
    }

    @GetMapping
    List<MaterialLotView> search(@RequestParam(required = false) String query) {
        return materialLotService.search(query);
    }

    @PutMapping("/{id}/stock")
    MaterialLotView replaceStock(@PathVariable UUID id, @Valid @RequestBody StockLevelRequest request) {
        return materialLotService.replaceStock(id, request.totalUnits());
    }
}
