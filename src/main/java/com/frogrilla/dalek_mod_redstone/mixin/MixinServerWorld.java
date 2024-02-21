package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld {
    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(BooleanSupplier bs, CallbackInfo info){
        ISonicStone.SONIC_STONE_INTERACTIONS.addAll(ISonicStone.INTERACTION_BUFFER);
        ISonicStone.INTERACTION_BUFFER.clear();
        List<SonicStoneInteraction> toDelete = new ArrayList<>();
        ISonicStone.SONIC_STONE_INTERACTIONS.forEach(interaction -> {
            if(interaction.ticksLeft == 0){
                Block block = interaction.world.getBlockState(interaction.blockPos).getBlock();
                if(block instanceof ISonicStone){
                    ((ISonicStone)block).Signal(interaction);
                }
                toDelete.add(interaction);
            }
            else{
                interaction.ticksLeft--;
            }
        });
        toDelete.forEach(ISonicStone.SONIC_STONE_INTERACTIONS::remove);
        // Thank you Jayson
    }
}
