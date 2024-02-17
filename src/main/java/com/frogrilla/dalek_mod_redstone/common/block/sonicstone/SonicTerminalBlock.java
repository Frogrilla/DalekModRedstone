package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicTerminalBlock extends Block implements ISonicStone{
    public static final VoxelShape SHAPE_UP = VoxelShapes.join(Block.box(1, 0, 1, 15, 2.5, 15), Block.box(5, 2.5, 5, 11, 15.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_DOWN = VoxelShapes.join(Block.box(1, 13.5, 1, 15, 16, 15), Block.box(5, 0.5, 5, 11, 13.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.join(Block.box(1, 1, 13.5, 15, 15, 16), Block.box(5, 5, 0.5, 11, 11, 13.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.join(Block.box(0, 1, 1, 2.5, 15, 15), Block.box(2.5, 5, 5, 15.5, 11, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.join(Block.box(1, 1, 0, 15, 15, 2.5), Block.box(5, 5, 2.5, 11, 11, 15.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.join(Block.box(13.5, 1, 1, 16, 15, 15), Block.box(0.5, 5, 5, 13.5, 11, 11), IBooleanFunction.OR);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SonicTerminalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(FACING, Direction.UP)
                        .setValue(POWERED, false)
                        .setValue(DELAY, 0)
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(DELAY);
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
                .setValue(FACING, context.getClickedFace());
    }
    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);
            if(nPower != state.getValue(POWERED)){
                world.setBlockAndUpdate(blockPos,state.setValue(POWERED, nPower));
                if(nPower && !state.getValue(ACTIVATED)) world.getBlockTicks().scheduleTick(blockPos, state.getBlock(), 0);
            }
        }
    }
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)){
            case UP:
                return SHAPE_UP;
            case DOWN:
                return SHAPE_DOWN;
            case NORTH:
                return SHAPE_NORTH;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
        }
        return SHAPE_UP;
    }

    @Override
    public boolean Signal(World world, BlockPos pos, int strength, Direction direction, int distance) {
        if(world.getBlockState(pos).getValue(ACTIVATED) || world.getBlockState(pos).getValue(POWERED)) return true;
        world.getBlockTicks().scheduleTick(pos, world.getBlockState(pos).getBlock(), distance);
        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // DELAY TIMER
        if(state.getValue(ACTIVATED)){
            int cur = state.getValue(DELAY)-1;
            if(cur == 0) {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, 0).setValue(ACTIVATED, false));
            }
            else {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, cur));
                world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
            }
            return;
        }

        // SONIC STONE PROCESS
        BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
        ISonicStone.SonicBlock(world, under);
        world.setBlockAndUpdate(pos, state.setValue(DELAY, DELAY_TIME).setValue(ACTIVATED, true));
        world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
    }
}
