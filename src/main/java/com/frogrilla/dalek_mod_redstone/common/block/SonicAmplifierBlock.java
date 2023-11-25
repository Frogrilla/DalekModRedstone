package com.frogrilla.dalek_mod_redstone.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicAmplifierBlock extends Block {
    public SonicAmplifierBlock(Properties builder) { super(builder); }

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
        return state.getValue(ACTIVATED) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(ACTIVATED, false);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random p_225534_4_) {
        world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false));
        super.tick(state, world, pos, p_225534_4_);
    }
}
