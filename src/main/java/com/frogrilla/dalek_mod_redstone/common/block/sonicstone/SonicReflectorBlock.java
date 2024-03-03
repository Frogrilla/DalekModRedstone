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
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SonicReflectorBlock extends Block implements ISonicStone {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty SIDE_1 = BooleanProperty.create("side_1");
    public static final BooleanProperty SIDE_2 = BooleanProperty.create("side_2");
    public SonicReflectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                    .setValue(FACING, Direction.UP)
                    .setValue(SIDE_1, true)
                    .setValue(SIDE_2, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(SIDE_1);
        builder.add(SIDE_2);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
            .setValue(FACING, context.getPlayer().isShiftKeyDown() ? context.getClickedFace().getOpposite() : context.getClickedFace());
    }

    public Direction GetReflectionDirection(Direction orientation, Direction direction){
        if(orientation.getAxis() == direction.getAxis()) return null;
        switch(orientation) {
            case UP:
                switch (direction) {
                    case NORTH:
                        return Direction.EAST;
                    case SOUTH:
                        return Direction.WEST;
                    case WEST:
                        return Direction.SOUTH;
                    case EAST:
                        return Direction.NORTH;
                }
                return null;
            case DOWN:
                switch (direction) {
                    case NORTH:
                        return Direction.WEST;
                    case SOUTH:
                        return Direction.EAST;
                    case WEST:
                        return Direction.NORTH;
                    case EAST:
                        return Direction.SOUTH;
                }
                return null;
            case NORTH:
                switch (direction) {
                    case UP:
                        return Direction.WEST;
                    case DOWN:
                        return Direction.EAST;
                    case WEST:
                        return Direction.UP;
                    case EAST:
                        return Direction.DOWN;
                }
                return null;
            case SOUTH:
                switch (direction) {
                    case UP:
                        return Direction.EAST;
                    case DOWN:
                        return Direction.WEST;
                    case WEST:
                        return Direction.DOWN;
                    case EAST:
                        return Direction.UP;
                }
                return null;
            case EAST:
                switch (direction) {
                    case NORTH:
                        return Direction.UP;
                    case SOUTH:
                        return Direction.DOWN;
                    case UP:
                        return Direction.NORTH;
                    case DOWN:
                        return Direction.SOUTH;
                }
                return null;
            case WEST:
                switch (direction) {
                    case NORTH:
                        return Direction.DOWN;
                    case SOUTH:
                        return Direction.UP;
                    case UP:
                        return Direction.SOUTH;
                    case DOWN:
                        return Direction.NORTH;
                }
                return null;
        }
        return null;
    }

    public BooleanProperty GetMirrorSide(Direction orientation, Direction direction){
        if(orientation.getAxis() == direction.getAxis()) return null;
        switch(orientation) {
            case UP:
                switch (direction) {
                    case NORTH:
                    case WEST:
                        return SIDE_1;
                    case SOUTH:
                    case EAST:
                        return SIDE_2;
                }
                return null;
            case DOWN:
                switch (direction) {
                    case NORTH:
                    case EAST:
                        return SIDE_1;
                    case SOUTH:
                    case WEST:
                        return SIDE_2;
                }
                return null;
            case NORTH:
                switch (direction) {
                    case UP:
                    case EAST:
                        return SIDE_1;
                    case DOWN:
                    case WEST:
                        return SIDE_2;
                }
                return null;
            case SOUTH:
                switch (direction) {
                    case UP:
                    case WEST:
                        return SIDE_1;
                    case DOWN:
                    case EAST:
                        return SIDE_2;
                }
                return null;
            case EAST:
                switch (direction) {
                    case NORTH:
                    case DOWN:
                        return SIDE_1;
                    case SOUTH:
                    case UP:
                        return SIDE_2;
                }
                return null;
            case WEST:
                switch (direction) {
                    case NORTH:
                    case UP:
                        return SIDE_1;
                    case SOUTH:
                    case DOWN:
                        return SIDE_2;
                }
                return null;
        }
        return null;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        BooleanProperty side = GetMirrorSide(state.getValue(FACING), rayTraceResult.getDirection().getOpposite());
        if(side == null) return ActionResultType.PASS;
        world.setBlockAndUpdate(pos, state.cycle(side));
        return ActionResultType.SUCCESS;
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        if(state.getValue(FACING).getAxis() == interaction.direction.getAxis() || !DisruptSignal(interaction)) return;
        Direction reflectionDirection = GetReflectionDirection(state.getValue(FACING), interaction.direction);
        ISonicStone.CreateSignal(interaction.world, interaction.blockPos, interaction.strength-interaction.distance, reflectionDirection);
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        BooleanProperty side = GetMirrorSide(state.getValue(FACING), interaction.direction);
        return (side == null || state.getValue(side));
    }
}
