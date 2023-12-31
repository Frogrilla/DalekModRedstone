package com.frogrilla.dalek_mod_redstone.common.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModTabs {

    public static final ItemGroup DMR_TAB = new ItemGroup("dmr_tab") {
        @Override
        public ItemStack makeIcon() {
            if(ModBlocks.SONIC_RESONATOR.isPresent()) {
                return new ItemStack(ModBlocks.SONIC_RESONATOR.get());
            }
            return new ItemStack(Items.REDSTONE);
        }
    };

}
