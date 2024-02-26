package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SonicReceiverBlock extends Block implements ISonicStone {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SonicReceiverBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);
            if(nPower != state.getValue(POWERED)){
                world.setBlockAndUpdate(blockPos,state.setValue(POWERED, nPower));
            }
        }
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        if(!DisruptSignal(interaction)) return;
        BlockStateProperties.FACING.getAllValues().forEach(directionValuePair -> {
            Direction dir = directionValuePair.value();
            BlockPos check = interaction.blockPos.relative(dir);
            ISonicStone.SonicBlock(interaction.world, check);
        });
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return !interaction.world.getBlockState(interaction.blockPos).getValue(POWERED);
    }
}
