package harou.echoing_sculk_sensors.block;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

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

    private static BlockBehaviour.Properties createEchoingSculkSensorSettings() {
        return BlockBehaviour.Properties.ofLegacyCopy(Blocks.SCULK_SENSOR);
    }

    private static Block register(String id, java.util.function.Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        ResourceKey<Block> key = keyOf(id);
        Block block = factory.apply(settings.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    private static ResourceKey<Block> keyOf(String id) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(EchoingSculkSensors.MOD_ID, id));
    }
}
