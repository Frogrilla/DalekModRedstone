package com.frogrilla.dalek_mod_redstone.common.init;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DalekModRedstone.MOD_ID);

    public static final RegistryObject<BasicParticleType> SONIC_RESONANCE =
            PARTICLE_TYPES.register("sonic_resonance", () -> new BasicParticleType(true));

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}
