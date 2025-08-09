package harou.echoing_sculk_sensors.item;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import harou.echoing_sculk_sensors.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModItems {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoingSculkSensors.MOD_ID);

    public static Item ECHOING_SCULK_SENSOR;

    public static void initialize() {
        try {
            LOGGER.info("Starting item registration...");
            if (ModBlocks.ECHOING_SCULK_SENSOR == null) {
                LOGGER.error("Block not registered when trying to register item");
                return;
            }
            ECHOING_SCULK_SENSOR = registerBlockItem(ModBlocks.ECHOING_SCULK_SENSOR);
            LOGGER.info("Successfully registered Echoing Sculk Sensor item: {}", ECHOING_SCULK_SENSOR.getRegistryEntry().registryKey().getValue());
        } catch (Exception e) {
            LOGGER.error("Failed to register Echoing Sculk Sensor item", e);
            throw new RuntimeException("Failed to register Echoing Sculk Sensor item", e);
        }
    }

    private static RegistryKey<Item> keyOf(RegistryKey<Block> blockKey) {
        return RegistryKey.of(RegistryKeys.ITEM, blockKey.getValue());
    }

    private static Item registerBlockItem(Block block) {
        return register(
            keyOf(block.getRegistryEntry().registryKey()), 
            itemSettings -> new BlockItem(block, itemSettings), 
            new Item.Settings().useBlockPrefixedTranslationKey()
        );
    }

    private static Item register(RegistryKey<Item> key, java.util.function.Function<Item.Settings, Item> factory, Item.Settings settings) {
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, key, item);
    }
}
