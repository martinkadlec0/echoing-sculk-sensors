package harou.echoing_sculk_sensors.block;

import com.mojang.serialization.MapCodec;

import harou.echoing_sculk_sensors.block.entity.EchoingSculkSensorBlockEntity;
import harou.echoing_sculk_sensors.block.entity.ModBlockEntities;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.Vibrations;

import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlock extends SculkSensorBlock {
    public static final MapCodec<EchoingSculkSensorBlock> CODEC = EchoingSculkSensorBlock.createCodec(EchoingSculkSensorBlock::new);
    public static final net.minecraft.state.property.EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final net.minecraft.state.property.EnumProperty<GameSoundEvent> STORED_SOUND = net.minecraft.state.property.EnumProperty.of("stored_sound", GameSoundEvent.class);
    public static final net.minecraft.state.property.BooleanProperty POWERED = net.minecraft.state.property.BooleanProperty.of("powered");

    public EchoingSculkSensorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STORED_SOUND, GameSoundEvent.NONE)
                .with(POWERED, false));
    }

    @Override
    public MapCodec<EchoingSculkSensorBlock> getCodec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState baseState = super.getPlacementState(ctx);
        if (baseState == null) return null;
        return baseState
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(STORED_SOUND, GameSoundEvent.NONE)
                .with(POWERED, false);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EchoingSculkSensorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient) {
            return EchoingSculkSensorBlock.validateTicker(type, ModBlockEntities.ECHOING_SCULK_SENSOR, 
                (worldx, pos, statex, blockEntity) -> Vibrations.Ticker.tick(worldx, blockEntity.getVibrationListenerData(), blockEntity.getVibrationCallback()));
        }
        return null;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // Like calibrated sculk sensor - don't emit power from facing direction
        if (direction != state.get(FACING)) {
            return super.getWeakRedstonePower(state, world, pos, direction);
        }
        return 0;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        GameSoundEvent gameSoundEvent = state.get(STORED_SOUND);
        return gameSoundEvent.getFrequency();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder); // Adds SCULK_SENSOR_PHASE, POWER, WATERLOGGED
        builder.add(FACING, STORED_SOUND, POWERED); // Add our custom properties
    }

    public static GameSoundEvent getStoredSound(BlockState state) {
        return state.get(STORED_SOUND);
    }

    public static boolean hasStoredSound(BlockState state) {
        return getStoredSound(state) != GameSoundEvent.NONE;
    }

    public static BlockState setStoredSound(BlockState state, GameSoundEvent gameSoundEvent) {
        return state.with(STORED_SOUND, gameSoundEvent);
    }

    public static BlockState clearStoredSound(BlockState state) {
        return state.with(STORED_SOUND, GameSoundEvent.NONE);
    }

    public static boolean isPowered(BlockState state) {
        return state.get(POWERED);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            // Check if powered from the opposite side of the facing direction (like calibrated sculk sensors)
            Direction facing = state.get(FACING);
            Direction powerSide = facing.getOpposite();
            boolean isPowered = world.getEmittedRedstonePower(pos.offset(powerSide), powerSide) > 0;
            boolean wasPowered = state.get(POWERED);
            
            // Only clear stored sound when transitioning from unpowered to powered
            if (isPowered && !wasPowered && hasStoredSound(state)) {
                // Clear both block state and block entity
                clearStoredSoundComplete(world, pos, state);

                // State gets modified during clear/cooldown
                state = world.getBlockState(pos);
            }
            
            // Update the powered state if it changed
            if (isPowered != wasPowered) {
                world.setBlockState(pos, state.with(POWERED, isPowered), Block.NOTIFY_ALL);
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    private void clearStoredSoundComplete(World world, BlockPos pos, BlockState state) {
        // Clear the stored GameEvent from block entity
        if (world.getBlockEntity(pos) instanceof EchoingSculkSensorBlockEntity blockEntity) {
            blockEntity.clearStoredGameEvent();
        }

        if (hasStoredSound(state)) {
            if (!(Boolean)state.get(WATERLOGGED)) {
                world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
                    SoundCategory.BLOCKS,
                    1.0F,
                    world.random.nextFloat() * 0.2F + 0.8F
                );
            }
        
            // Clear the frequency from block state and reset to cooldown using parent method
            BlockState newState = clearStoredSound(state);
            SculkSensorBlock.setCooldown(world, pos, newState);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            // Clear the stored sound and reset to cooldown phase
            clearStoredSoundComplete(world, pos, state);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public int getCooldownTime() {
        return 10;
    }

}
