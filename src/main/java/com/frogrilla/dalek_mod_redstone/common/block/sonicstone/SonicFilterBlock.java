package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SonicFilterBlock extends Block implements ISonicStone {
    public static final IntegerProperty POWER = IntegerProperty.create("power", 1, 15);
    public static final IntegerProperty MODE = IntegerProperty.create("mode",0,2);
    public SonicFilterBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                    .setValue(POWER, 1)
                    .setValue(MODE, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(MODE);
        super.createBlockStateDefinition(builder);
    }
    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            int signalStrength = world.getBestNeighborSignal(blockPos);
            if(signalStrength > 0 && signalStrength != state.getValue(POWER)){
                world.setBlockAndUpdate(blockPos, state.setValue(POWER, signalStrength));
            }
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(world.isClientSide) return ActionResultType.PASS;
        world.setBlockAndUpdate(blockPos, state.cycle(MODE));
        return ActionResultType.SUCCESS;
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        return;
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        switch(state.getValue(MODE)){
            case 0:
                return !(interaction.strength < state.getValue(POWER));
            case 1:
                return !(interaction.strength > state.getValue(POWER));
            case 2:
                return interaction.strength != state.getValue(POWER);
        }
        return false;
    }
}
