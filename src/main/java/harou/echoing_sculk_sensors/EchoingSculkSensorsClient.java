package harou.echoing_sculk_sensors;

import harou.echoing_sculk_sensors.block.ModBlocks;
import harou.echoing_sculk_sensors.client.EchoingSculkSensorColorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

@Environment(EnvType.CLIENT)
public class EchoingSculkSensorsClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Set the render layer for the Echoing Sculk Sensor to CUTOUT
        // This ensures it renders with transparency like the Calibrated Sculk Sensor
        BlockRenderLayerMap.putBlock(ModBlocks.ECHOING_SCULK_SENSOR, BlockRenderLayer.CUTOUT);
        
        // Register color provider for dynamic echo shard tinting
        EchoingSculkSensorColorProvider.register();
    }
}
