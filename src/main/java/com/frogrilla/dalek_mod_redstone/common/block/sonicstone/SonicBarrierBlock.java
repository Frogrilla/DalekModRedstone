package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;


import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class SonicBarrierBlock extends Block implements ISonicStone {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public SonicBarrierBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
        builder.add(UP);
        builder.add(DOWN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (world.isClientSide || hand != Hand.MAIN_HAND) return ActionResultType.PASS;

        if (player.getItemInHand(hand).isEmpty()){
            world.setBlockAndUpdate(pos, state.cycle(getPropertyFromDirection(rayTraceResult.getDirection())));
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
    public static BooleanProperty getPropertyFromDirection(Direction dir){
        switch(dir){
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
        }
        return NORTH;
    }

    public static boolean getStateFromDirection(Direction dir, BlockState state){
        return state.getValue(getPropertyFromDirection(dir));
    }
    @Override
    public void Signal(SonicStoneInteraction interaction) {

    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return !getStateFromDirection(interaction.direction.getOpposite(), interaction.world.getBlockState(interaction.blockPos));
    }
}