package com.frogrilla.dalek_mod_redstone;

import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.init.ModItems;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicSonicStone;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicSonicResonator;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DalekModRedstone.MOD_ID)
public class DalekModRedstone
{
    //test
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "dalek_mod_redstone";

    public DalekModRedstone() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);

        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::runLater);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(ModBlocks.CLICK_DETECTOR.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.REMOTE_LOCK.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.STATTENHEIM_PANEL.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_RELAY.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_DIRECTOR.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_TERMINAL.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_BOOSTER.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_GATE.get(), RenderType.translucent());
        });
    }

    private void runLater(ParallelDispatchEvent event) {
        event.enqueueWork(() -> {
            DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_RESONATOR.get(), new SonicSonicResonator());
            DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_RELAY.get(), new SonicSonicStone());
            DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_DIRECTOR.get(), new SonicSonicStone());
            DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_TERMINAL.get(), new SonicSonicStone());
            DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_DISPLAY.get(), new SonicSonicStone());
        });
    }
}
