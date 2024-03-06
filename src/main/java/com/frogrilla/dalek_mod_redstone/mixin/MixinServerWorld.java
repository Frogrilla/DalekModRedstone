package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.init.ModParticles;
import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneSignal;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
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
        // Thank you Jayson
        ISonicStone.SONIC_STONE_SIGNALS.addAll(ISonicStone.SIGNAL_BUFFER);
        ISonicStone.SIGNAL_BUFFER.clear();
        List<SonicStoneSignal> deadSignals = new ArrayList<>();
        ISonicStone.SONIC_STONE_SIGNALS.forEach(signal -> {
            if(signal.tickCounter % ISonicStone.TICKS_PER_BLOCK == 0 && signal.tickCounter != signal.strength*ISonicStone.TICKS_PER_BLOCK){
                signal.distance++;
                BlockPos checkPos = signal.blockPos.relative(signal.direction, signal.distance);
                SonicStoneInteraction interaction = new SonicStoneInteraction(signal.world, checkPos, signal.direction, signal.strength, signal.distance);
                Block block = interaction.world.getBlockState(checkPos).getBlock();
                if(block instanceof ISonicStone){
                    ((ISonicStone)block).Signal(interaction);
                    if(((ISonicStone)block).DisruptSignal(interaction)){
                        deadSignals.add(signal);
                        return;
                    }
                }
                else if (DMSonicRegistry.SONIC_LOOKUP.containsKey(block)){
                    ISonicStone.SonicBlock(signal.world, checkPos);
                }
                signal.world.getServer().getLevel(signal.world.dimension()).sendParticles(ModParticles.SONIC_RESONANCE.get(), checkPos.getX()+0.5, checkPos.getY()+0.5, checkPos.getZ()+0.5, interaction.strength, 0.1, 0.1,0.1, 0.01f);
            }
            signal.tickCounter -= 1;
            if(signal.tickCounter <= 0) deadSignals.add(signal);
        });
        deadSignals.forEach(ISonicStone.SONIC_STONE_SIGNALS::remove);
        deadSignals.clear();
    }
}
