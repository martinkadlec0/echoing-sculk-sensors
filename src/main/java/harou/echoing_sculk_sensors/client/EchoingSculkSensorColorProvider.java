package harou.echoing_sculk_sensors.client;

import harou.echoing_sculk_sensors.block.EchoingSculkSensorBlock;
import harou.echoing_sculk_sensors.block.ModBlocks;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorColorProvider {
    
    public static void register() {
        ColorProviderRegistry.BLOCK.register(new EchoingSculkSensorBlockColorProvider(), ModBlocks.ECHOING_SCULK_SENSOR);
    }
    
    private static class EchoingSculkSensorBlockColorProvider implements BlockColorProvider {
        @Override
        public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
            // tintIndex = 1 means it's the echo shard texture
            if (tintIndex == 1 && state.getBlock() instanceof EchoingSculkSensorBlock) {
                GameSoundEvent storedSound = EchoingSculkSensorBlock.getStoredSound(state);                
                
                if (storedSound == GameSoundEvent.NONE) {
                    return 0x666666; // Dim gray tint (multiply with texture)
                }
            }
            
            // Default color for other tint indices
            return 0xFFFFFF;
        }
    }
}
