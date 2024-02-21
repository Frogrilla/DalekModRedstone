package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicDisplayBlock extends Block implements ISonicStone {

    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
    public SonicDisplayBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                    .setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        return state.getValue(POWER);
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(POWER, interaction.strength+1 - interaction.distance));
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return true;
    }
}
