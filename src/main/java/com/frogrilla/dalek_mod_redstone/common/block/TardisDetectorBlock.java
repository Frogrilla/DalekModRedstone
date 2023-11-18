package com.frogrilla.dalek_mod_redstone.common.block;

import com.swdteam.common.block.tardis.TardisBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tileentity.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class TardisDetectorBlock extends Block {
    public TardisDetectorBlock(Properties builder) { super(builder); }

    private int next = 0;
    static final IntegerProperty DETECTED = IntegerProperty.create("detected",0,2);

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
        return state.getValue(DETECTED) == 2 ? 15 : 0;
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
                .setValue(DETECTED, 0);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        world.setBlockAndUpdate(pos, state.setValue(DETECTED, next));
        super.tick(state, world, pos, rand);
    }

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            Block above = world.getBlockState(blockPos.offset(0,1,0)).getBlock();
            boolean isTardis = above == DMBlocks.TARDIS.get();
            if(isTardis){
                TardisTileEntity tardis = (TardisTileEntity) world.getBlockEntity(blockPos.above());

                if(tardis.state == TardisState.REMAT && state.getValue(DETECTED) == 0){
                    next = 2;
                    world.getBlockTicks().scheduleTick(blockPos,this, 200);
                } else if (tardis.state == TardisState.DEMAT && state.getValue(DETECTED) == 2) {
                    world.setBlockAndUpdate(blockPos, state.setValue(DETECTED, 0));
                }
            }
            else{
                if(state.getValue(DETECTED) != 0){
                    world.setBlockAndUpdate(blockPos, state.setValue(DETECTED, 0));
                }
            }
        }
    }
}
