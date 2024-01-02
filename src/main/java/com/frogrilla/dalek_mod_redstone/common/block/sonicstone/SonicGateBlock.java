package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicGateBlock extends Block{
    public static final VoxelShape SHAPE_X = Block.box(7, 0, 0, 9, 16, 16);
    public static final VoxelShape SHAPE_Y = Block.box(0, 7, 0, 16, 9, 16);
    public static final VoxelShape SHAPE_Z = Block.box(0, 0, 7, 16, 16, 9);

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public SonicGateBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(AXIS, Direction.Axis.Z)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state.setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader iBlockReader, BlockPos pos, ISelectionContext context) {
        switch(state.getValue(AXIS)){
            case X: return SHAPE_X;
            case Y: return SHAPE_Y;
            case Z: return SHAPE_Z;
        }
        return SHAPE_Y;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Direction throughDir = getAxisDirection(state.getValue(AXIS));
        BlockStateProperties.FACING.getAllValues().forEach(pair -> {
            Direction direction = pair.value();
            if(direction != throughDir && direction != throughDir.getOpposite()){
                BlockPos targetPos = pos.relative(direction);
                BlockState targetState = world.getBlockState(targetPos);
                SonicStoneBlock.sonicBlock(world, targetPos, targetState);
            }
        });
    }

    public static boolean directionMatchesAxis(Direction.Axis axis, Direction dir){
        Direction axisDirection = getAxisDirection(axis);
        return axisDirection == dir || axisDirection.getOpposite() == dir;
    }

    public static Direction getAxisDirection(Direction.Axis axis){
        switch (axis){
            case X:
                return Direction.EAST;
            case Y:
                return Direction.UP;
            case Z:
                return Direction.NORTH;
        }
        return null;
    }
}
