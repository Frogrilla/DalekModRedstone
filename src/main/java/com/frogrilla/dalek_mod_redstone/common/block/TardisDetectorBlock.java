package com.frogrilla.dalek_mod_redstone.common.block;

import com.swdteam.common.init.DMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TardisDetectorBlock extends Block {
    public TardisDetectorBlock(Properties builder) { super(builder); }

    static final BooleanProperty DETECTED = BooleanProperty.create("detected");

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
        return state.getValue(DETECTED) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DETECTED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(DETECTED, false);
    }

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            Block above = world.getBlockState(blockPos.offset(0,1,0)).getBlock();
            boolean isTardis = above == DMBlocks.TARDIS.get();

            if(isTardis != state.getValue(DETECTED)){
                world.setBlockAndUpdate(blockPos, state.setValue(DETECTED, isTardis));
            }
        }
    }
}
