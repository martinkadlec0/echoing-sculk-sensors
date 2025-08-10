package harou.echoing_sculk_sensors.block;

import com.mojang.serialization.MapCodec;
import harou.echoing_sculk_sensors.block.entity.EchoingSculkSensorBlockEntity;
import harou.echoing_sculk_sensors.block.entity.ModBlockEntities;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import harou.echoing_sculk_sensors.block.enums.GameSoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class EchoingSculkSensorBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<EchoingSculkSensorBlock> CODEC = EchoingSculkSensorBlock.createCodec(EchoingSculkSensorBlock::new);
    public static final net.minecraft.state.property.EnumProperty<SculkSensorPhase> SCULK_SENSOR_PHASE = Properties.SCULK_SENSOR_PHASE;
    public static final net.minecraft.state.property.IntProperty POWER = Properties.POWER;
    public static final net.minecraft.state.property.BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final net.minecraft.state.property.EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final net.minecraft.state.property.EnumProperty<GameSoundEvent> STORED_SOUND = net.minecraft.state.property.EnumProperty.of("stored_sound", GameSoundEvent.class);

    public EchoingSculkSensorBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)
                .with(POWER, 0)
                .with(WATERLOGGED, false)
                .with(FACING, Direction.NORTH)
                .with(STORED_SOUND, GameSoundEvent.NONE));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return this.getDefaultState()
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        SculkSensorPhase phase = getPhase(state);
        if (phase == SculkSensorPhase.ACTIVE) {
            setCooldown(world, pos, state);
        } else if (phase == SculkSensorPhase.COOLDOWN) {
            world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), Block.NOTIFY_ALL);
            if (!state.get(WATERLOGGED)) {
                world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.2f + 0.8f);
            }
        }
    }

    private static void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        world.updateNeighbors(pos, block);
        world.updateNeighbors(pos.down(), block);
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
                (worldx, pos, statex, blockEntity) -> {
                    if (blockEntity instanceof EchoingSculkSensorBlockEntity && worldx instanceof ServerWorld) {
                        ((EchoingSculkSensorBlockEntity)blockEntity).tick((ServerWorld)worldx, pos, statex);
                    }
                });
        }
        return null;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? state.get(POWER) : 0;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        GameSoundEvent gameSoundEvent = state.get(STORED_SOUND);
        return gameSoundEvent.getFrequency();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SCULK_SENSOR_PHASE, POWER, WATERLOGGED, FACING, STORED_SOUND);
    }

    public static SculkSensorPhase getPhase(BlockState state) {
        return state.get(SCULK_SENSOR_PHASE);
    }

    public static boolean isInactive(BlockState state) {
        return getPhase(state) == SculkSensorPhase.INACTIVE;
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

    public static void setCooldown(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN).with(POWER, 0), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), 10);
        updateNeighbors(world, pos, state);
    }

    public void setActive(@Nullable Entity sourceEntity, World world, BlockPos pos, BlockState state, int power, int frequency) {
        world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE).with(POWER, power), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), 10);
        updateNeighbors(world, pos, state);
        if (!state.get(WATERLOGGED)) {
            world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.2f + 0.8f);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            // Check if powered from the opposite side of the facing direction (like calibrated sculk sensors)
            Direction facing = state.get(FACING);
            Direction powerSide = facing.getOpposite();
            if (world.getEmittedRedstonePower(pos.offset(powerSide), powerSide) > 0) {
                // Clear the stored sound and reset to cooldown phase when powered
                if (hasStoredSound(state)) {
                    // Clear both block state and block entity
                    clearStoredSoundComplete(world, pos, state);
                }
            }
        }
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
    }

    private void clearStoredSoundComplete(World world, BlockPos pos, BlockState state) {
        // Clear the stored GameEvent from block entity
        if (world.getBlockEntity(pos) instanceof EchoingSculkSensorBlockEntity blockEntity) {
            blockEntity.clearStoredGameEvent();
        }
        
        // Clear the frequency from block state and reset to cooldown
        BlockState newState = clearStoredSound(state).with(SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN).with(POWER, 0);
        world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, state.getBlock(), 10);
        updateNeighbors(world, pos, newState);
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
}
