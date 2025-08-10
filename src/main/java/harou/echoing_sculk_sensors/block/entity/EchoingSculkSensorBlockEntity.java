package harou.echoing_sculk_sensors.block.entity;

import harou.echoing_sculk_sensors.block.EchoingSculkSensorBlock;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;

import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlockEntity extends SculkSensorBlockEntity {
    @Nullable
    private RegistryKey<GameEvent> storedGameEvent = null;

    public EchoingSculkSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECHOING_SCULK_SENSOR, pos, state);
    }

    @Override
    public Vibrations.Callback createCallback() {
        return new EchoingVibrationCallback(this.getPos());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        
        // Read stored game event ID
        String gameEventId = view.getString("stored_game_event", "");
        if (!gameEventId.isEmpty()) {
            this.storedGameEvent = RegistryKey.of(RegistryKeys.GAME_EVENT, Identifier.of(gameEventId));
        } else {
            this.storedGameEvent = null;
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        
        // Write stored game event ID
        if (this.storedGameEvent != null) {
            view.putString("stored_game_event", this.storedGameEvent.getValue().toString());
        } else {
            view.putString("stored_game_event", "");
        }
    }

    @Nullable
    public RegistryKey<GameEvent> getStoredGameEvent() {
        return this.storedGameEvent;
    }

    public void setStoredGameEvent(@Nullable RegistryKey<GameEvent> gameEvent) {
        this.storedGameEvent = gameEvent;
        this.markDirty();
    }

    public boolean hasStoredGameEvent() {
        return this.storedGameEvent != null;
    }

    public void clearStoredGameEvent() {
        this.storedGameEvent = null;
        this.markDirty();
    }

    public void tick(ServerWorld world, BlockPos pos, BlockState state) {
        Vibrations.Ticker.tick(world, this.getVibrationListenerData(), this.getVibrationCallback());
    }

    protected class EchoingVibrationCallback extends SculkSensorBlockEntity.VibrationCallback {
        public static final int RANGE = 16;

        public EchoingVibrationCallback(BlockPos pos) {
            super(pos);
        }

        @Override
        public int getRange() {
            return RANGE;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
            // First check the parent's basic acceptance rules
            if (!super.accepts(world, pos, event, emitter)) {
                return false;
            }
            
            BlockState blockState = EchoingSculkSensorBlockEntity.this.getCachedState();
            GameSoundEvent gameSoundEvent = EchoingSculkSensorBlock.getStoredSound(blockState);
            
            // If we have a stored event, only accept that exact same event
            if (gameSoundEvent != GameSoundEvent.NONE) {
                return event.getKey().map(key -> GameSoundEvent.fromRegistryKey(key) == gameSoundEvent).orElse(false);
            }
            
            // If we don't have a stored event, accept any sound (first sound to be stored)
            return true;
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState blockState = EchoingSculkSensorBlockEntity.this.getCachedState();
            if (!SculkSensorBlock.isInactive(blockState)) return;

            RegistryKey<GameEvent> eventKey = event.getKey().orElse(null);
            GameSoundEvent gameSoundEvent = EchoingSculkSensorBlock.getStoredSound(blockState);
            
            // If we don't have a stored event yet, store this one
            if (gameSoundEvent == GameSoundEvent.NONE && eventKey != null) {
                // Convert the GameEvent to our enum
                GameSoundEvent newGameSoundEvent = GameSoundEvent.fromRegistryKey(eventKey);
                if (newGameSoundEvent != GameSoundEvent.NONE) {
                    // Store the exact GameEvent in the block entity for persistence
                    EchoingSculkSensorBlockEntity.this.setStoredGameEvent(eventKey);
                    
                    // Store the enum value in the block state
                    BlockState newState = EchoingSculkSensorBlock.setStoredSound(blockState, newGameSoundEvent);
                    world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
                    blockState = newState; // Update our reference
                    gameSoundEvent = newGameSoundEvent;
                }
            }
            
            // Only activate if this is the exact stored event OR if no event is stored yet
            if (eventKey != null && (GameSoundEvent.fromRegistryKey(eventKey) == gameSoundEvent || gameSoundEvent == GameSoundEvent.NONE)) {
                // Let parent handle the activation - this will call our overridden setActive method
                super.accept(world, pos, event, sourceEntity, entity, distance);
            }
        }


    }
}
