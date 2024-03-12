package com.frogrilla.dalek_mod_redstone;

import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.init.ModItems;
import com.frogrilla.dalek_mod_redstone.common.init.ModParticles;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DalekModRedstone.MOD_ID)
public class DalekModRedstone
{
    //test
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "dalek_mod_redstone";
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public DalekModRedstone() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);
        ModParticles.register(modEventBus);

        modEventBus.addListener(this::doClientStuff);

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
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_REFLECTOR.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SONIC_ADDER.get(), RenderType.translucent());
        });
    }
}
