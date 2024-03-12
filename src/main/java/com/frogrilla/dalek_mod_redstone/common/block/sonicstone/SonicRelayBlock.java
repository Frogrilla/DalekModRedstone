package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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

public class SonicRelayBlock extends Block implements ISonicStone {
    public static final VoxelShape SHAPE_UP = VoxelShapes.join(Block.box(1, 0, 1, 15, 2.5, 15), Block.box(5, 2.5, 5, 11, 15.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_DOWN = VoxelShapes.join(Block.box(1, 13.5, 1, 15, 16, 15), Block.box(5, 0.5, 5, 11, 13.5, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_NORTH = VoxelShapes.join(Block.box(1, 1, 13.5, 15, 15, 16), Block.box(5, 5, 0.5, 11, 11, 13.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_EAST = VoxelShapes.join(Block.box(0, 1, 1, 2.5, 15, 15), Block.box(2.5, 5, 5, 15.5, 11, 11), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_SOUTH = VoxelShapes.join(Block.box(1, 1, 0, 15, 15, 2.5), Block.box(5, 5, 2.5, 11, 11, 15.5), IBooleanFunction.OR);
    public static final VoxelShape SHAPE_WEST = VoxelShapes.join(Block.box(13.5, 1, 1, 16, 15, 15), Block.box(0.5, 5, 5, 13.5, 11, 11), IBooleanFunction.OR);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SonicRelayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(FACING, Direction.UP)
                        .setValue(POWERED, false)
                        .setValue(ACTIVATED, false)
                        .setValue(BOOST, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(ACTIVATED);
        builder.add(BOOST);
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
            int nPower = world.getBestNeighborSignal(blockPos);
            boolean powered = nPower > 0;
            boolean activated = state.getValue(ACTIVATED);
            if(powered != state.getValue(POWERED)){
                if(powered && !activated) Signal(new SonicStoneInteraction(world, blockPos, null, nPower+1, 0));
                world.setBlockAndUpdate(blockPos,state.setValue(POWERED, powered).setValue(ACTIVATED, activated || powered));
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
    public void Signal(SonicStoneInteraction interaction) {
        if(interaction.world.isClientSide()) return;
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        if(state.getValue(ACTIVATED) || state.getValue(POWERED)) return;

        BlockPos under = interaction.blockPos.relative(state.getValue(FACING).getOpposite());
        ISonicStone.SonicBlock(interaction.world, under);

        FACING.getAllValues().forEach(pair -> {
            Direction direction = pair.value();
            if(interaction.direction != null && direction == interaction.direction.getOpposite()) return;
            if(direction == state.getValue(FACING).getOpposite()) return;
            ISonicStone.CreateSignal(interaction.world, interaction.blockPos, state.getValue(BOOST) ? interaction.sourceStrength : interaction.strength, direction);
        });

        interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(ACTIVATED, true));
        interaction.world.getBlockTicks().scheduleTick(interaction.blockPos, this, DELAY_TIME);
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (world.isClientSide || hand != Hand.MAIN_HAND) return ActionResultType.PASS;

        if (player.getItemInHand(hand).isEmpty()){
            world.setBlockAndUpdate(pos, state.cycle(BOOST));
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        // Hehehehe - the piston won't see it coming
        return true;
    }
}
