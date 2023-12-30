package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.SonicBarrierBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
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

public class SonicRelayBlock extends SonicStoneBlock {
    public static final VoxelShape SHAPE_FLOOR = VoxelShapes.join(Block.box(1, 0, 1, 15, 2.5, 15), Block.box(5, 2.5, 5, 11, 15.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_CEILING = VoxelShapes.join(Block.box(1, 13.5, 1, 15, 16, 15), Block.box(5, 0.5, 5, 11, 13.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.join(Block.box(1, 1, 13.5, 15, 15, 16), Block.box(5, 5, 0.5, 11, 11, 13.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.join(Block.box(0, 1, 1, 2.5, 15, 15), Block.box(2.5, 5, 5, 15.5, 11, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.join(Block.box(1, 1, 0, 15, 15, 2.5), Block.box(5, 5, 2.5, 11, 11, 15.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.join(Block.box(13.5, 1, 1, 16, 15, 15), Block.box(0.5, 5, 5, 13.5, 11, 11), IBooleanFunction.OR);

    public static final DirectionProperty FACING = DirectionProperty.create("facing", direction -> true);
    public static final IntegerProperty MODE = IntegerProperty.create("mode", 0, 2);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public static final int SEARCH_DISTANCE = 8;

    public SonicRelayBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(ACTIVATED, false)
                        .setValue(DELAY, 0)
                        .setValue(RECEIVE_DIR, Direction.DOWN)
                        .setValue(FACING, Direction.UP)
                        .setValue(MODE, 0)
                        .setValue(POWERED, false)
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
        builder.add(MODE);
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
            .setValue(FACING, context.getClickedFace())
            .setValue(RECEIVE_DIR, context.getClickedFace().getOpposite())
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

    public void createSignals(World world, BlockState state, BlockPos pos){
        if(state.getValue(MODE) == 1){
            sendSignal(world, state, pos, state.getValue(FACING), SEARCH_DISTANCE);
        }
        else {
            RECEIVE_DIR.getAllValues().forEach(pair -> {
                Direction direction = pair.value();
                if(state.getValue(MODE) == 0 && direction == state.getValue(RECEIVE_DIR)) return;
                if(direction == state.getValue(FACING).getOpposite()) return;
                sendSignal(world, state, pos, direction, SEARCH_DISTANCE);
            });
        }
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
    public void onDeactivate(World world, BlockPos pos, BlockState state) {
        world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false).setValue(RECEIVE_DIR, state.getValue(FACING).getOpposite()).setValue(DELAY, 0));
    }

    @Override
    public void onActivate(World world, BlockPos pos, BlockState state) {
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