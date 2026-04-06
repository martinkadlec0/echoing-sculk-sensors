package harou.echoing_sculk_sensors.data;

import harou.echoing_sculk_sensors.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class LootTableGenerator extends FabricBlockLootSubProvider {
	public LootTableGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void generate() {
		// Generate loot table for echoing sculk sensor - only drops itself with silk touch
		add(ModBlocks.ECHOING_SCULK_SENSOR, createSilkTouchOnlyTable(ModBlocks.ECHOING_SCULK_SENSOR));
	}
}
