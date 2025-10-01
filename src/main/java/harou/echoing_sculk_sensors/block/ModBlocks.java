package harou.echoing_sculk_sensors.block;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {    
    public static Block ECHOING_SCULK_SENSOR;

    public static void initialize() {
        try {
            ECHOING_SCULK_SENSOR = register("echoing_sculk_sensor", 
                settings -> new EchoingSculkSensorBlock(settings), 
                createEchoingSculkSensorSettings());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register Echoing Sculk Sensor block", e);
        }
    }

    private static AbstractBlock.Settings createEchoingSculkSensorSettings() {
        return AbstractBlock.Settings.copyShallow(Blocks.SCULK_SENSOR);
    }

    private static Block register(String id, java.util.function.Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = keyOf(id);
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static RegistryKey<Block> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(EchoingSculkSensors.MOD_ID, id));
    }
}
