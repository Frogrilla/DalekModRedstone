package com.frogrilla.dalek_mod_redstone.common.sonic;

import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.SonicCategory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicSonicStone implements DMSonicRegistry.ISonicInteraction {
    public SonicSonicStone(){}
    @Override
    public void interact(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        if (o instanceof BlockPos && !world.isClientSide) {
            BlockPos p = (BlockPos) o;
            BlockState state = world.getBlockState(p);
            if(!state.getValue(ISonicStone.ACTIVATED)){
                ((ISonicStone)state.getBlock()).Signal(new SonicStoneInteraction(world, p, null, ISonicStone.STRENGTH, 0));
            }
        }
    }

    @Override
    public int scanTime() {
        return 5;
    }

    @Override
    public boolean disableDefaultInteraction(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        return false;
    }

    @Override
    public SonicCategory getCategory() {
        return SonicCategory.REDSTONE;
    }
}
