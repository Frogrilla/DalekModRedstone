package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class SonicBoosterBlock extends Block implements ISonicStone {

    public SonicBoosterBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        if(interaction.world.isClientSide()) return;
        BlockState state = interaction.world.getBlockState(interaction.blockPos);

        ISonicStone.CreateSignal(interaction.world, interaction.blockPos, interaction.sourceStrength, interaction.direction);
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return true;
    }
}
