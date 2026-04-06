package harou.echoing_sculk_sensors.data;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import harou.echoing_sculk_sensors.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
	public BlockTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		EchoingSculkSensors.LOGGER.info("Generating block tags for Echoing Sculk Sensors...");
		
		// Add echoing sculk sensor to the hoe mineable tag
		valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE)
			.add(ModBlocks.ECHOING_SCULK_SENSOR);
		
		EchoingSculkSensors.LOGGER.info("Block tags generated successfully!");
	}
}
