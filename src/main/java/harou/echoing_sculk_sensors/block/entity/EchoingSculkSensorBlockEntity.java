package harou.echoing_sculk_sensors.block.entity;

import harou.echoing_sculk_sensors.block.EchoingSculkSensorBlock;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlockEntity extends SculkSensorBlockEntity {
	@Nullable
	private ResourceKey<GameEvent> storedGameEvent = null;

	public EchoingSculkSensorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ECHOING_SCULK_SENSOR, pos, state);
	}

	@Override
	public VibrationSystem.User createVibrationUser() {
		return new EchoingVibrationCallback(this.getBlockPos());
	}

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);
		
		// Read stored game event ID
		String gameEventId = view.getStringOr("stored_game_event", "");
		if (!gameEventId.isEmpty()) {
			this.storedGameEvent = ResourceKey.create(Registries.GAME_EVENT, Identifier.parse(gameEventId));
		} else {
			this.storedGameEvent = null;
		}
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);
		
		// Write stored game event ID
		if (this.storedGameEvent != null) {
			view.putString("stored_game_event", this.storedGameEvent.identifier().toString());
		} else {
			view.putString("stored_game_event", "");
		}
	}

	@Nullable
	public ResourceKey<GameEvent> getStoredGameEvent() {
		return this.storedGameEvent;
	}

	public void setStoredGameEvent(@Nullable ResourceKey<GameEvent> gameEvent) {
		this.storedGameEvent = gameEvent;
		this.setChanged();
	}

	public boolean hasStoredGameEvent() {
		return this.storedGameEvent != null;
	}

	public void clearStoredGameEvent() {
		this.storedGameEvent = null;
		this.setChanged();
	}

	public void tick(ServerLevel world, BlockPos pos, BlockState state) {
		VibrationSystem.Ticker.tick(world, this.getVibrationData(), this.getVibrationUser());
	}

	protected class EchoingVibrationCallback extends SculkSensorBlockEntity.VibrationUser {
		public static final int RANGE = 16;

		public EchoingVibrationCallback(BlockPos pos) {
			super(pos);
		}

		@Override
		public int getListenerRadius() {
			return LISTENER_RANGE;
		}

		@Override
		public boolean canReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable GameEvent.Context emitter) {
			// First check the parent's basic acceptance rules
			if (!super.canReceiveVibration(world, pos, event, emitter)) {
				return false;
			}
			
			BlockState blockState = EchoingSculkSensorBlockEntity.this.getBlockState();
			GameSoundEvent gameSoundEvent = EchoingSculkSensorBlock.getStoredSound(blockState);
			
			// If we have a stored event, only accept that exact same event
			if (gameSoundEvent != GameSoundEvent.NONE) {
				return event.unwrapKey().map(key -> GameSoundEvent.fromRegistryKey(key) == gameSoundEvent).orElse(false);
			}
			
			// If we don't have a stored event, accept any sound (first sound to be stored)
			return true;
		}

		@Override
		public void onReceiveVibration(ServerLevel world, BlockPos pos, Holder<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
			BlockState blockState = EchoingSculkSensorBlockEntity.this.getBlockState();
			if (!SculkSensorBlock.canActivate(blockState)) return;

			ResourceKey<GameEvent> eventKey = event.unwrapKey().orElse(null);
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
					world.setBlock(this.blockPos, newState, Block.UPDATE_ALL);
					blockState = newState; // Update our reference
					gameSoundEvent = newGameSoundEvent;
				}
			}
			
			// Only activate if this is the exact stored event OR if no event is stored yet
			if (eventKey != null && (GameSoundEvent.fromRegistryKey(eventKey) == gameSoundEvent || gameSoundEvent == GameSoundEvent.NONE)) {
				// Let parent handle the activation - this will call our overridden setActive method
				super.onReceiveVibration(world, pos, event, sourceEntity, entity, distance);
			}
		}


	}
}
