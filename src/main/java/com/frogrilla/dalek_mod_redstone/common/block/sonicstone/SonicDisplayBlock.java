package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SonicDisplayBlock extends Block implements ISonicStone {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SonicDisplayBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                    .setValue(POWER, 0)
                    .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        builder.add(POWERED);
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

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            int singalStrength = world.getBestNeighborSignal(blockPos);
            boolean powered = singalStrength > 0;
            if(powered){
                world.setBlockAndUpdate(blockPos, state.setValue(POWERED, powered).setValue(POWER, singalStrength));
            }
            else{
                world.setBlockAndUpdate(blockPos, state.setValue(POWERED, powered));
            }
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public void Signal(SonicStoneInteraction interaction) {
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        if(state.getValue(POWERED)) return;
        interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(POWER, interaction.strength));
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return false;
    }
}
