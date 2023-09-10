package com.frogrilla.dalek_mod_redstone.common.init;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.ClickDetectorBlock;
import com.frogrilla.dalek_mod_redstone.common.block.RemoteLockBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS
            = DeferredRegister.create(ForgeRegistries.BLOCKS, DalekModRedstone.MOD_ID);

    public static RegistryObject<Block>


            CLICK_DETECTOR = registerBlock("click_detector",
            () -> new ClickDetectorBlock(AbstractBlock.Properties.of(Material.STONE).instabreak())),
            REMOTE_LOCK = registerBlock("remote_lock",
                    () -> new RemoteLockBlock(AbstractBlock.Properties.of(Material.STONE).instabreak())


            );

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));
    }
    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
