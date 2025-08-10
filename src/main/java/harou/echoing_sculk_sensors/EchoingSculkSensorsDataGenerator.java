package harou.echoing_sculk_sensors;

import harou.echoing_sculk_sensors.data.BlockTagGenerator;
import harou.echoing_sculk_sensors.data.LootTableGenerator;
import harou.echoing_sculk_sensors.data.RecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EchoingSculkSensorsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(RecipeGenerator::new);
		pack.addProvider(LootTableGenerator::new);
		pack.addProvider(BlockTagGenerator::new);
	}
}
