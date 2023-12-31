package com.frogrilla.dalek_mod_redstone.common.sonic;

import com.frogrilla.dalek_mod_redstone.common.block.SonicResonatorBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.SonicCategory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicSonicResonator implements DMSonicRegistry.ISonicInteraction{

    public SonicSonicResonator(){}
    @Override
    public void interact(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        if (o instanceof BlockPos) {
            BlockPos p = (BlockPos) o;
            BlockState state = world.getBlockState(p);
            if(state.getBlock() == ModBlocks.SONIC_RESONATOR.get()){
                if(!state.getValue(SonicResonatorBlock.ACTIVATED)){
                    world.setBlockAndUpdate(p, state.setValue(SonicResonatorBlock.ACTIVATED, true));
                    int mode = state.getValue(SonicResonatorBlock.FREQUENCY);
                    world.getBlockTicks().scheduleTick(p, ModBlocks.SONIC_RESONATOR.get(), (mode+1)*20);
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
