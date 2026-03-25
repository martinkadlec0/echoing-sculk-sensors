package harou.echoing_sculk_sensors.block;

import com.mojang.serialization.MapCodec;

import harou.echoing_sculk_sensors.block.entity.EchoingSculkSensorBlockEntity;
import harou.echoing_sculk_sensors.block.entity.ModBlockEntities;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlock extends SculkSensorBlock {
    public static final MapCodec<EchoingSculkSensorBlock> CODEC = EchoingSculkSensorBlock.simpleCodec(EchoingSculkSensorBlock::new);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<GameSoundEvent> STORED_SOUND = net.minecraft.world.level.block.state.properties.EnumProperty.create("stored_sound", GameSoundEvent.class);
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty POWERED = net.minecraft.world.level.block.state.properties.BooleanProperty.create("powered");

    public EchoingSculkSensorBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(STORED_SOUND, GameSoundEvent.NONE)
                .setValue(POWERED, false));
    }

    @Override
    public MapCodec<EchoingSculkSensorBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState baseState = super.getStateForPlacement(ctx);
        if (baseState == null) return null;
        return baseState
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(STORED_SOUND, GameSoundEvent.NONE)
                .setValue(POWERED, false);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EchoingSculkSensorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClientSide()) {
            return EchoingSculkSensorBlock.createTickerHelper(type, ModBlockEntities.ECHOING_SCULK_SENSOR, 
                (worldx, pos, statex, blockEntity) -> VibrationSystem.Ticker.tick(worldx, blockEntity.getVibrationData(), blockEntity.getVibrationUser()));
        }
        return null;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        // Like calibrated sculk sensor - don't emit power from facing direction
        if (direction != state.getValue(FACING)) {
            return super.getSignal(state, world, pos, direction);
        }
        return 0;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        GameSoundEvent gameSoundEvent = state.getValue(STORED_SOUND);
        return gameSoundEvent.getFrequency();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder); // Adds SCULK_SENSOR_PHASE, POWER, WATERLOGGED
        builder.add(FACING, STORED_SOUND, POWERED); // Add our custom properties
    }

    public static GameSoundEvent getStoredSound(BlockState state) {
        return state.getValue(STORED_SOUND);
    }

    public static boolean hasStoredSound(BlockState state) {
        return getStoredSound(state) != GameSoundEvent.NONE;
    }

    public static BlockState setStoredSound(BlockState state, GameSoundEvent gameSoundEvent) {
        return state.setValue(STORED_SOUND, gameSoundEvent);
    }

    public static BlockState clearStoredSound(BlockState state) {
        return state.setValue(STORED_SOUND, GameSoundEvent.NONE);
    }

    public static boolean isPowered(BlockState state) {
        return state.getValue(POWERED);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if (!world.isClientSide()) {
            // Check if powered from the opposite side of the facing direction (like calibrated sculk sensors)
            Direction facing = state.getValue(FACING);
            Direction powerSide = facing.getOpposite();
            boolean isPowered = world.getSignal(pos.relative(powerSide), powerSide) > 0;
            boolean wasPowered = state.getValue(POWERED);
            
            // Only clear stored sound when transitioning from unpowered to powered
            if (isPowered && !wasPowered && hasStoredSound(state)) {
                // Clear both block state and block entity
                clearStoredSoundComplete(world, pos, state);

                // State gets modified during clear/cooldown
                state = world.getBlockState(pos);
            }
            
            // Update the powered state if it changed
            if (isPowered != wasPowered) {
                world.setBlock(pos, state.setValue(POWERED, isPowered), Block.UPDATE_ALL);
            }
        }
        super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    private void clearStoredSoundComplete(Level level, BlockPos pos, BlockState state) {
        // Clear the stored GameEvent from block entity
        if (level.getBlockEntity(pos) instanceof EchoingSculkSensorBlockEntity blockEntity) {
            blockEntity.clearStoredGameEvent();
        }

        if (hasStoredSound(state)) {
            if (!(Boolean)state.getValue(WATERLOGGED)) {
                level.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.SCULK_CLICKING,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.2F + 0.8F
                );
            }
        
            // Clear the frequency from block state and reset to cooldown using parent method
            BlockState newState = clearStoredSound(state);
            SculkSensorBlock.deactivate(level, pos, newState);
        }
    }

    @Override
	protected boolean useShapeForLightOcclusion(final BlockState state) {
		return false;
	}

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide()) {
            // Clear the stored sound and reset to cooldown phase
            clearStoredSoundComplete(world, pos, state);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public int getActiveTicks() {
        return 10;
    }

}
