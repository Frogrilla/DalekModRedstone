package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
    public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 2);
    public static final IntegerProperty DELAY = IntegerProperty.create("delay", 0, 8);

    public SonicRelayBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(FACING, Direction.UP)
                        .setValue(ACTIVATED, false)
                        .setValue(BLOCK_DIR, Direction.DOWN)
                        .setValue(MODE, 0)
                        .setValue(DELAY, 0)
        );
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
        builder.add(MODE);
        builder.add(DELAY);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
            .setValue(FACING, context.getClickedFace())
            .setValue(BLOCK_DIR, context.getClickedFace().getOpposite())
        ;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (world.isClientSide || hand != Hand.MAIN_HAND) return ActionResultType.PASS;

        if (player.getItemInHand(hand).isEmpty()){
            world.setBlockAndUpdate(pos, state.cycle(MODE));
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.getValue(ACTIVATED)){
            if(state.getValue(DELAY) == 1){
                world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false).setValue(BLOCK_DIR, state.getValue(FACING).getOpposite()).setValue(DELAY, 0));
            }
            else {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, state.getValue(DELAY)-1));
                world.getBlockTicks().scheduleTick(pos, this, 1);
            }
        }
        else{
            world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
            BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
            BlockState underState = world.getBlockState(under);
            if (DMSonicRegistry.SONIC_LOOKUP.containsKey(underState.getBlock()) && underState.getBlock() != ModBlocks.SONIC_RELAY.get()) {
                DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction)DMSonicRegistry.SONIC_LOOKUP.get(underState.getBlock());
                if(son != null) son.interact(world, null, null, under);
            }
            createSignals(world, state, pos);
            world.setBlockAndUpdate(pos, state.setValue(DELAY, 4).setValue(ACTIVATED, true));
            world.getBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    public void createSignals(World world, BlockState state, BlockPos pos){
        if(state.getValue(MODE) == 1){
            sendSignal(world, state, pos, state.getValue(FACING));
        }
        else {
            BLOCK_DIR.getAllValues().forEach(pair -> {
                Direction direction = pair.value();
                if(state.getValue(MODE) == 0 && direction == state.getValue(BLOCK_DIR)) return;
                if(direction == state.getValue(FACING).getOpposite()) return;
                sendSignal(world, state, pos, direction);
            });
        }
    }

    public boolean sendSignal(World world, BlockState state, BlockPos pos, Direction dir){
        for(int i = 1; i <= SEARCH_DISTANCE; ++i){
            BlockPos checkPos = pos.relative(dir, i);
            BlockState checkState = world.getBlockState(checkPos);

            if(checkState.getBlock() == ModBlocks.SONIC_BARRIER.get() && !SonicBarrierBlock.getStateFromDirection(dir.getOpposite(), checkState)) return false;
            if(checkState.getBlock() == ModBlocks.SONIC_RELAY.get()){
                if(checkState.getValue(DELAY) <= i){
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
