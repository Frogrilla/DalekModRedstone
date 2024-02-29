package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.init.ModParticles;
import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import net.minecraft.block.Block;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

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
                    interaction.world.getServer().getLevel(interaction.world.dimension()).sendParticles(ModParticles.SONIC_RESONANCE.get(), interaction.blockPos.getX(), interaction.blockPos.getY(), interaction.blockPos.getZ(), 10, 0.1, 0.1,0.1, 0.01f);
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
