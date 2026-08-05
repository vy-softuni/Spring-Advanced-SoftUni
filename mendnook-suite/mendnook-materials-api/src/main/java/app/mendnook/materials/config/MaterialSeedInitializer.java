package app.mendnook.materials.config;

import app.mendnook.materials.stock.MaterialFamily;
import app.mendnook.materials.stock.MaterialLot;
import app.mendnook.materials.stock.MaterialLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test")
public class MaterialSeedInitializer {

    @Bean
    CommandLineRunner seedMaterialDatabase(MaterialLotRepository repository) {
        return arguments -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new MaterialLot("Braided lamp cable", MaterialFamily.ELECTRICAL, "metres", 35),
                        new MaterialLot("Insulated terminal pair", MaterialFamily.ELECTRICAL, "pairs", 50),
                        new MaterialLot("No. 5 zipper slider", MaterialFamily.TEXTILE, "pieces", 24),
                        new MaterialLot("Heavy cotton repair patch", MaterialFamily.TEXTILE, "pieces", 32),
                        new MaterialLot("M4 stainless fastener", MaterialFamily.FASTENER, "pieces", 90),
                        new MaterialLot("Two-part repair adhesive", MaterialFamily.ADHESIVE, "tubes", 16),
                        new MaterialLot("Bicycle brake cable", MaterialFamily.BICYCLE, "pieces", 18)
                ));
            }
        };
    }
}
