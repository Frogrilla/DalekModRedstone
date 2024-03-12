package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.block.ClickDetectorBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicNoteBlock;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicSonicDisplay;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicSonicResonator;
import com.frogrilla.dalek_mod_redstone.common.sonic.SonicSonicStone;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.datapack.SonicInteractionDataReloadListener;
import com.swdteam.network.packets.PacketClickTardisDoors;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(SonicInteractionDataReloadListener.class)
public abstract class MixinSonicInteractionDataReloadListener extends JsonReloadListener {
    public MixinSonicInteractionDataReloadListener(Gson p_i51536_1_, String p_i51536_2_) {
        super(p_i51536_1_, p_i51536_2_);
    }

    @Inject(at = @At("TAIL"), remap = false, method = "apply(Ljava/util/Map;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;)V")
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo info) {
        DMSonicRegistry.SONIC_LOOKUP.put(Blocks.NOTE_BLOCK, new SonicNoteBlock());
        DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_RESONATOR.get(), new SonicSonicResonator());
        DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_RELAY.get(), new SonicSonicStone(4));
        DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_DIRECTOR.get(), new SonicSonicStone(4));
        DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_TERMINAL.get(), new SonicSonicStone(4));
        DMSonicRegistry.SONIC_LOOKUP.put(ModBlocks.SONIC_DISPLAY.get(), new SonicSonicDisplay());
    }
}
