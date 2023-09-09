package com.frogrilla.dalek_mod_redstone;

import com.frogrilla.dalek_mod_redstone.block.ModBlocks;
import com.frogrilla.dalek_mod_redstone.item.ModItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(com.frogrilla.dalek_mod_redstone.dalek_mod_redstone.MOD_ID)
public class dalek_mod_redstone
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "dalek_mod_redstone";

    public dalek_mod_redstone() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        modEventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(ModBlocks.CLICK_DETECTOR.get(), RenderType.cutout());
        });
    }
}
