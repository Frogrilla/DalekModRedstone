package com.frogrilla.dalek_mod_redstone.block;

import com.frogrilla.dalek_mod_redstone.dalek_mod_redstone;
import com.frogrilla.dalek_mod_redstone.item.ModItems;
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
            = DeferredRegister.create(ForgeRegistries.BLOCKS, dalek_mod_redstone.MOD_ID);

    public static final RegistryObject<Block> CLICK_DETECTOR = registerBlock("click_detector",
            () -> new ClickDetectorBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()));

    public static final RegistryObject<Block> REMOTE_LOCK = registerBlock("remote_lock",
            () -> new RemoteLockBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()));

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
