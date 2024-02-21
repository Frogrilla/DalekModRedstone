package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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

public class SonicBoosterBlock extends Block implements ISonicStone {

    public SonicBoosterBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        if(interaction.world.isClientSide()) return;
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        if(state.getValue(ACTIVATED)) return;

        ISonicStone.SendSignal(interaction.world, interaction.blockPos, SEARCH_DISTANCE, interaction.direction);

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
}
