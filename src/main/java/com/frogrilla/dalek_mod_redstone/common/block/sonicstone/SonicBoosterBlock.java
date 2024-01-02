package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SonicBoosterBlock extends SonicStoneBlock{
    public SonicBoosterBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void onActivate(World world, BlockPos pos, BlockState state) {
        sendSignal(world, state, pos, state.getValue(RECEIVE_DIR).getOpposite(), SEARCH_DISTANCE);
        world.getBlockTicks().scheduleTick(pos, this, 1);
    }
}
