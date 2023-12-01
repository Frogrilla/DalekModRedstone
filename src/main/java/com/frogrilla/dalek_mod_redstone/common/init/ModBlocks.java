package com.frogrilla.dalek_mod_redstone.common.init;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.*;
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


            CLICK_DETECTOR = registerBlock("click_detector", () -> new ClickDetectorBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()), ModTabs.DMR_TAB),
            REMOTE_LOCK = registerBlock("remote_lock", () -> new RemoteLockBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()), ModTabs.DMR_TAB),
            TARDIS_DETECTOR = registerBlock("tardis_detector", () -> new TardisDetectorBlock(AbstractBlock.Properties.of(Material.HEAVY_METAL)), ModTabs.DMR_TAB),
            STATTENHEIM_PANEL = registerBlock("stattenheim_panel", () -> new StattenheimPanelBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()), ModTabs.DMR_TAB),
            DOOR_PANEL = registerBlock("door_panel", () -> new DoorPanelBlock(AbstractBlock.Properties.of(Material.STONE).instabreak()), ModTabs.DMR_TAB),
            SONIC_RESONATOR = registerBlock("sonic_resonator", () -> new SonicResonatorBlock(AbstractBlock.Properties.of(Material.HEAVY_METAL)), ModTabs.DMR_TAB);
    ;

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, ItemGroup tab){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, ItemGroup tab){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }


    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        return registerBlock(name, block, ItemGroup.TAB_REDSTONE);
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
