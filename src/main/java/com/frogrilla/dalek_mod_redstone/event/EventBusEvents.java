package com.frogrilla.dalek_mod_redstone.event;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.init.ModParticles;
import com.frogrilla.dalek_mod_redstone.common.particle.SonicResonanceParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DalekModRedstone.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event){
        Minecraft.getInstance().particleEngine.register(ModParticles.SONIC_RESONANCE.get(), SonicResonanceParticle.Factory::new);
    }
}
