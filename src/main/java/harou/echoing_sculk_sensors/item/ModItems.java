package harou.echoing_sculk_sensors.item;

import harou.echoing_sculk_sensors.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModItems {

	public static Item ECHOING_SCULK_SENSOR;

	public static void initialize() {
		try {
			ECHOING_SCULK_SENSOR = registerBlockItem(ModBlocks.ECHOING_SCULK_SENSOR);
		} catch (Exception e) {
			throw new RuntimeException("Failed to register Echoing Sculk Sensor item", e);
		}
	}

	private static ResourceKey<Item> keyOf(ResourceKey<Block> blockKey) {
		return ResourceKey.create(Registries.ITEM, blockKey.identifier());
	}

	private static Item registerBlockItem(Block block) {
		return register(
			keyOf(block.builtInRegistryHolder().key()), 
			itemSettings -> new BlockItem(block, itemSettings), 
			new Item.Properties().useBlockDescriptionPrefix()
		);
	}

	private static Item register(ResourceKey<Item> key, java.util.function.Function<Item.Properties, Item> factory, Item.Properties settings) {
		Item item = factory.apply(settings.setId(key));
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}
}
