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
    static final IntegerProperty DETECTED = IntegerProperty.create("detected",0,3);

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
        return state.getValue(DETECTED) == 3 ? 15 : 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) { return true; }

    @Override
    public int getAnalogOutputSignal(BlockState state, World p_180641_2_, BlockPos p_180641_3_) {
        switch(state.getValue(DETECTED)){
            case 0: return 0;
            case 1: return 1;
            case 2: return 8;
            case 3: return 15;
        }
        return 0;
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
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        world.getBlockTicks().scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random p_225534_4_) {
        checkAbove(pos, state, world);
        world.getBlockTicks().scheduleTick(pos, this, 1);
        super.tick(state, world, pos, p_225534_4_);
    }

//    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
//        if (!world.isClientSide){
//            checkAbove(blockPos, state, world);
//        }
//    }

    void checkAbove(BlockPos blockPos, BlockState state, World world){
        if (!world.isClientSide){
            Block above = world.getBlockState(blockPos.offset(0,1,0)).getBlock();
            boolean isTardis = above == DMBlocks.TARDIS.get();

            int target = 0;

            if(isTardis) {
                world.setBlockAndUpdate(blockPos, state);
                TardisTileEntity tardis = (TardisTileEntity) world.getBlockEntity(blockPos.above());

                switch(tardis.state){
                    case DEMAT:
                        target = 1;
                        break;
                    case REMAT:
                        target = 2;
                        break;
                    case NEUTRAL:
                        target = 3;
                        break;
                }
            }

            if(state.getValue(DETECTED) != target) {
                world.setBlockAndUpdate(blockPos, state.setValue(DETECTED, target));
            }
        }
    }

}
