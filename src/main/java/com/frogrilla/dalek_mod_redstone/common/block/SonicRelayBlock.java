package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
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

public class SonicRelayBlock extends Block {
    public static final int SEARCH_DISTANCE = 8;
    public static final VoxelShape SHAPE_FLOOR = VoxelShapes.join(Block.box(1, 0, 1, 15, 2.5, 15), Block.box(5, 2.5, 5, 11, 15.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_CEILING = VoxelShapes.join(Block.box(1, 13.5, 1, 15, 16, 15), Block.box(5, 0.5, 5, 11, 13.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.join(Block.box(1, 1, 13.5, 15, 15, 16), Block.box(5, 5, 0.5, 11, 11, 13.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.join(Block.box(0, 1, 1, 2.5, 15, 15), Block.box(2.5, 5, 5, 15.5, 11, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.join(Block.box(1, 1, 0, 15, 15, 2.5), Block.box(5, 5, 2.5, 11, 11, 15.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.join(Block.box(13.5, 1, 1, 16, 15, 15), Block.box(0.5, 5, 5, 13.5, 11, 11), IBooleanFunction.OR);

    public static final DirectionProperty FACING = DirectionProperty.create("facing", direction -> true);
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final DirectionProperty BLOCK_DIR = DirectionProperty.create("block_dir", direction -> true);

    public SonicRelayBlock(Properties builder) {
        super(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)){
            case UP:
                return SHAPE_FLOOR;
            case DOWN:
                return SHAPE_CEILING;
            case NORTH:
                return SHAPE_NORTH;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
        }
        return SHAPE_FLOOR;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(ACTIVATED);
        builder.add(BLOCK_DIR);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
            .setValue(ACTIVATED, false)
            .setValue(FACING, context.getClickedFace())
            .setValue(BLOCK_DIR, context.getClickedFace().getOpposite())
        ;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.getValue(ACTIVATED)){
            world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false).setValue(BLOCK_DIR, state.getValue(FACING).getOpposite()));
        }
        else{
            world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
            BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
            BlockState underState = world.getBlockState(under);
            if (DMSonicRegistry.SONIC_LOOKUP.containsKey(underState.getBlock()) && underState.getBlock() != ModBlocks.SONIC_RELAY.get()) {
                DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction)DMSonicRegistry.SONIC_LOOKUP.get(underState.getBlock());
                if(son != null) son.interact(world, null, null, under);
            }
            createSignal(world, state, pos);
            world.getBlockTicks().scheduleTick(pos, this, 8);
        }
    }

    public void createSignal(World world, BlockState state, BlockPos pos){
        BLOCK_DIR.getAllValues().forEach(pair -> {
            Direction direction = pair.value();
            if(direction == state.getValue(BLOCK_DIR)) return;
            if(direction == state.getValue(FACING).getOpposite()) return;
            sendSignal(world, state, pos, direction);
        });
    }

    public boolean sendSignal(World world, BlockState state, BlockPos pos, Direction dir){
        for(int i = 1; i <= SEARCH_DISTANCE; ++i){
            BlockPos checkPos = pos.relative(dir, i);
            BlockState checkState = world.getBlockState(checkPos);

            if(checkState.getBlock() == Blocks.BEDROCK) return false;
            if(checkState.getBlock() == ModBlocks.SONIC_RELAY.get()){
                if(!checkState.getValue(ACTIVATED)){
                    world.setBlockAndUpdate(checkPos, checkState.setValue(BLOCK_DIR, dir.getOpposite()));
                    world.getBlockTicks().scheduleTick(checkPos, this, i);
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
