package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicDirectorBlock extends SonicRelayBlock {
    public SonicDirectorBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void onActivate(World world, BlockPos pos, BlockState state) {
        BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
        BlockState underState = world.getBlockState(under);
        if (!(underState.getBlock() instanceof SonicStoneBlock)) {
            sonicBlock(world, under, underState);
        }
        sendSignal(world, state, pos, state.getValue(FACING), SEARCH_DISTANCE);
        world.getBlockTicks().scheduleTick(pos, this, 1);
    }
}
