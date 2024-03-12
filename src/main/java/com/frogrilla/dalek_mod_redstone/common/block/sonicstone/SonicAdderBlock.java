package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicAdderBlock extends Block implements ISonicStone {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public SonicAdderBlock(AbstractBlock.Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(NORTH, false)
                        .setValue(EAST, false)
                        .setValue(SOUTH, false)
                        .setValue(WEST, false)
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(FACING, Direction.UP)
                        .setValue(POWER, 0)
                        .setValue(ACTIVATED, false)
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
        builder.add(FACING);
        builder.add(POWER);
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
    public static boolean anySideActivated(BlockState state){
        return state.getValue(NORTH)
            || state.getValue(SOUTH)
            || state.getValue(EAST)
            || state.getValue(WEST)
            || state.getValue(UP)
            || state.getValue(DOWN);
    }
    @Override
    public void Signal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        if(state.getValue(ACTIVATED) || interaction.direction.getAxis() == state.getValue(FACING).getAxis() || getStateFromDirection(interaction.direction.getOpposite(), state)) return;
        if(anySideActivated(state)){
            int power = state.getValue(POWER);
            power += interaction.strength;
            power = Math.min(power,15);

            BlockPos under = interaction.blockPos.relative(state.getValue(FACING).getOpposite());
            ISonicStone.SonicBlock(interaction.world, under);
            ISonicStone.CreateSignal(interaction.world, interaction.blockPos, power, state.getValue(FACING));

            interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(ACTIVATED, true).setValue(getPropertyFromDirection(interaction.direction.getOpposite()), true).setValue(POWER, power));
            interaction.world.getBlockTicks().scheduleTick(interaction.blockPos, this, DELAY_TIME);
        }
        else{
            if(getStateFromDirection(interaction.direction.getOpposite(), state)) return;
            interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(getPropertyFromDirection(interaction.direction.getOpposite()), true).setValue(POWER, interaction.strength));
        }
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockAndUpdate(pos, state
                .setValue(POWER, 0)
                .setValue(ACTIVATED, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
