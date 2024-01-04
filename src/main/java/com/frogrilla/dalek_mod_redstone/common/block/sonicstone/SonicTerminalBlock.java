package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicTerminalBlock extends SonicRelayBlock{
    public SonicTerminalBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void onActivate(World world, BlockPos pos, BlockState state) {
        BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
        BlockState underState = world.getBlockState(under);
        if (!(underState.getBlock() instanceof SonicStoneBlock)) {
            sonicBlock(world, under, underState);
        }
    }
}
