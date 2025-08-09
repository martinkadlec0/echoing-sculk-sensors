package harou.echoing_sculk_sensors.block.entity;

import harou.echoing_sculk_sensors.EchoingSculkSensors;
import harou.echoing_sculk_sensors.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<EchoingSculkSensorBlockEntity> ECHOING_SCULK_SENSOR;

    public static void initialize() {
        ECHOING_SCULK_SENSOR = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(EchoingSculkSensors.MOD_ID, "echoing_sculk_sensor"),
            FabricBlockEntityTypeBuilder.<EchoingSculkSensorBlockEntity>create(EchoingSculkSensorBlockEntity::new, ModBlocks.ECHOING_SCULK_SENSOR).build()
        );
    }
}
