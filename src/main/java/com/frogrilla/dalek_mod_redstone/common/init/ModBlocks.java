package com.frogrilla.dalek_mod_redstone.common.init;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.*;
import com.frogrilla.dalek_mod_redstone.common.block.SonicResonatorBlock;
import com.frogrilla.dalek_mod_redstone.common.block.sonicstone.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
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


            CLICK_DETECTOR = registerBlock("click_detector", () -> new ClickDetectorBlock(AbstractBlock.Properties.of(Material.WOOD).instabreak().sound(SoundType.WOOD)), ModTabs.DMR_TAB),
            REMOTE_LOCK = registerBlock("remote_lock", () -> new RemoteLockBlock(AbstractBlock.Properties.of(Material.WOOD).instabreak().sound(SoundType.WOOD)), ModTabs.DMR_TAB),
            TARDIS_DETECTOR = registerBlock("tardis_detector", () -> new TardisDetectorBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)), ModTabs.DMR_TAB),
            STATTENHEIM_PANEL = registerBlock("stattenheim_panel", () -> new StattenheimPanelBlock(AbstractBlock.Properties.of(Material.WOOD).instabreak().sound(SoundType.WOOD)), ModTabs.DMR_TAB),
            SONIC_RESONATOR = registerBlock("sonic_resonator", () -> new SonicResonatorBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_RELAY = registerBlock("sonic_relay", () -> new SonicRelayBlock(AbstractBlock.Properties.of(Material.GLASS).instabreak().sound(SoundType.GLASS).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_DIRECTOR = registerBlock("sonic_director", () -> new SonicDirectorBlock(AbstractBlock.Properties.of(Material.GLASS).instabreak().sound(SoundType.GLASS).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_TERMINAL = registerBlock("sonic_terminal", () -> new SonicTerminalBlock(AbstractBlock.Properties.of(Material.GLASS).instabreak().sound(SoundType.GLASS).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_BOOSTER = registerBlock("sonic_booster", () -> new SonicBoosterBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_BARRIER = registerBlock("sonic_barrier", () -> new SonicBarrierBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD)), ModTabs.DMR_TAB),
            SONIC_GATE = registerBlock("sonic_gate", () -> new SonicGateBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_DISPLAY = registerBlock("sonic_display", () -> new SonicDisplayBlock(AbstractBlock.Properties.of(Material.WOOD).sound(SoundType.WOOD).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_REFLECTOR = registerBlock("sonic_reflector", () -> new SonicReflectorBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).instabreak().noOcclusion()), ModTabs.DMR_TAB),
            SONIC_RECEIVER = registerBlock("sonic_receiver", () -> new SonicReceiverBlock(AbstractBlock.Properties.of(Material.GLASS).sound(SoundType.GLASS).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_FILTER = registerBlock("sonic_filter", () -> new SonicFilterBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).sound(SoundType.METAL).isRedstoneConductor((a,b,c) -> false)), ModTabs.DMR_TAB),
            SONIC_ADDER = registerBlock("sonic_adder", () -> new SonicAdderBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).sound(SoundType.METAL).isRedstoneConductor((a,b,c) -> false).noOcclusion()), ModTabs.DMR_TAB)
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
