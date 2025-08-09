package harou.echoing_sculk_sensors.block;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoingSculkSensors.MOD_ID);
    
    public static Block ECHOING_SCULK_SENSOR;

    public static void initialize() {
        try {
            LOGGER.info("Starting block registration...");
            ECHOING_SCULK_SENSOR = register("echoing_sculk_sensor", 
                settings -> new EchoingSculkSensorBlock(settings), 
                createEchoingSculkSensorSettings());
            LOGGER.info("Successfully registered Echoing Sculk Sensor block: {}", ECHOING_SCULK_SENSOR.getRegistryEntry().registryKey().getValue());
        } catch (Exception e) {
            LOGGER.error("Failed to register Echoing Sculk Sensor block", e);
            throw new RuntimeException("Failed to register Echoing Sculk Sensor block", e);
        }
    }

    private static AbstractBlock.Settings createEchoingSculkSensorSettings() {
        return AbstractBlock.Settings.create()
            .mapColor(MapColor.CYAN)
            .strength(1.5f)
            .sounds(BlockSoundGroup.SCULK_SENSOR)
            .luminance(state -> 1)
            .emissiveLighting((state, world, pos) -> SculkSensorPhase.ACTIVE == state.get(EchoingSculkSensorBlock.SCULK_SENSOR_PHASE));
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
