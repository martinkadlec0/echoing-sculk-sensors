package harou.echoing_sculk_sensors;

import harou.echoing_sculk_sensors.client.EchoingSculkSensorColorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EchoingSculkSensorsClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Set the render layer for the Echoing Sculk Sensor to CUTOUT
        // This ensures it renders with transparency like the Calibrated Sculk Sensor
        // Should no longer be neccesary? https://fabricmc.net/2026/03/14/261.html#automatically-set-render-layers
        // ChunkSectionLayer.putBlock(ModBlocks.ECHOING_SCULK_SENSOR, ChunkSectionLayer.CUTOUT);
        
        // Register color provider for dynamic echo shard tinting
        EchoingSculkSensorColorProvider.register();
    }
}
