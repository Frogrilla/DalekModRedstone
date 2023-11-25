package com.frogrilla.dalek_mod_redstone.common.sonic;

import com.frogrilla.dalek_mod_redstone.common.block.SonicAmplifierBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.SonicCategory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicSonicAmplifier implements DMSonicRegistry.ISonicInteraction{

    public SonicSonicAmplifier(){}
    @Override
    public void interact(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        if (o instanceof BlockPos) {
            BlockPos p = (BlockPos) o;
            BlockState state = world.getBlockState(p);
            if(state.getBlock() == ModBlocks.SONIC_AMPLIFIER.get()){
                if(!state.getValue(SonicAmplifierBlock.ACTIVATED)){
                    world.setBlockAndUpdate(p, state.setValue(SonicAmplifierBlock.ACTIVATED, true));
                    world.getBlockTicks().scheduleTick(p, ModBlocks.SONIC_AMPLIFIER.get(), 40);
                }
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
