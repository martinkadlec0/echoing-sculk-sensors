package harou.echoing_sculk_sensors.block.entity;

import harou.echoing_sculk_sensors.block.EchoingSculkSensorBlock;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlockEntity extends SculkSensorBlockEntity {
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback = this.createCallback();
    @Nullable
    private RegistryKey<GameEvent> storedGameEvent = null;


    public EchoingSculkSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ECHOING_SCULK_SENSOR, pos, state);
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener(this);
    }

    @Override
    public Vibrations.Callback createCallback() {
        return new EchoingVibrationCallback(this.getPos());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.listenerData = view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
        
        // Read stored game event ID
        String gameEventId = view.getString("stored_game_event", "");
        if (!gameEventId.isEmpty()) {
            this.storedGameEvent = RegistryKey.of(net.minecraft.registry.RegistryKeys.GAME_EVENT, Identifier.of(gameEventId));
        } else {
            this.storedGameEvent = null;
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("listener", Vibrations.ListenerData.CODEC, this.listenerData);
        
        // Write stored game event ID
        if (this.storedGameEvent != null) {
            view.putString("stored_game_event", this.storedGameEvent.getValue().toString());
        } else {
            view.putString("stored_game_event", "");
        }
    }

    @Override
    public Vibrations.ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Vibrations.Callback getVibrationCallback() {
        return this.callback;
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

    @Override
    public Vibrations.VibrationListener getEventListener() {
        return this.listener;
    }

    public void tick(ServerWorld world, BlockPos pos, BlockState state) {
        Vibrations.Ticker.tick(world, this.getVibrationListenerData(), this.getVibrationCallback());
    }

    protected class EchoingVibrationCallback implements Vibrations.Callback {
        public static final int RANGE = 16;
        protected final BlockPos pos;
        private final PositionSource positionSource;

        public EchoingVibrationCallback(BlockPos pos) {
            this.pos = pos;
            this.positionSource = new BlockPositionSource(pos);
        }

        @Override
        public int getRange() {
            return 16;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean triggersAvoidCriterion() {
            return true;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
            if (pos.equals(this.pos) && (event.matches(GameEvent.BLOCK_DESTROY) || event.matches(GameEvent.BLOCK_PLACE))) {
                return false;
            }
            if (Vibrations.getFrequency(event) == 0) {
                return false;
            }
            
            BlockState blockState = EchoingSculkSensorBlockEntity.this.getCachedState();
            GameSoundEvent gameSoundEvent = EchoingSculkSensorBlock.getStoredSound(blockState);
            
            // If we have a stored event, only accept that exact same event
            if (gameSoundEvent != GameSoundEvent.NONE) {
                return event.getKey().map(key -> GameSoundEvent.fromRegistryKey(key) == gameSoundEvent).orElse(false);
            }
            
            // If we don't have a stored event, accept any sound (first sound to be stored)
            return EchoingSculkSensorBlock.isInactive(blockState);
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState blockState = EchoingSculkSensorBlockEntity.this.getCachedState();
            if (EchoingSculkSensorBlock.isInactive(blockState)) {
                int frequency = Vibrations.getFrequency(event);
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
                
                // Only activate if this is the exact stored event
                if (eventKey != null && GameSoundEvent.fromRegistryKey(eventKey) == gameSoundEvent && gameSoundEvent != GameSoundEvent.NONE) {
                    int signalStrength = Vibrations.getSignalStrength(distance, this.getRange());
                    Block block = blockState.getBlock();
                    if (block instanceof EchoingSculkSensorBlock) {
                        EchoingSculkSensorBlock echoingSculkSensorBlock = (EchoingSculkSensorBlock)block;
                        echoingSculkSensorBlock.setActive(sourceEntity, world, this.pos, blockState, signalStrength, frequency);
                    }
                }
            }
        }

        @Override
        public void onListen() {
            EchoingSculkSensorBlockEntity.this.markDirty();
        }

        @Override
        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}
