package harou.echoing_sculk_sensors.client;

import harou.echoing_sculk_sensors.block.EchoingSculkSensorBlock;
import harou.echoing_sculk_sensors.block.ModBlocks;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EchoingSculkSensorColorProvider {
    
    public static void register() {
        // BlockTintSource for tintindex 0 (block body)
        BlockTintSource bodyTintSource = new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return -1;
            }
        };
        
        // BlockTintSource for tintindex 1 (echo shard)
        BlockTintSource shardTintSource = new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                if (state.getBlock() instanceof EchoingSculkSensorBlock) {
                    GameSoundEvent storedSound = EchoingSculkSensorBlock.getStoredSound(state);
                    
                    if (storedSound == GameSoundEvent.NONE) {
                        return 0xFF666666; // Dim gray tint (ARGB: fully opaque dark gray)
                    }
                }
                
                // Default color when sound is stored
                return -1;
            }
        };
        
        BlockColorRegistry.register(List.of(bodyTintSource, shardTintSource), ModBlocks.ECHOING_SCULK_SENSOR);
    }
}
