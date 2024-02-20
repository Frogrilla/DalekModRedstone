package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.block.ClickDetectorBlock;
import com.swdteam.common.init.DMBlockEntities;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.common.tileentity.tardis.DoubleDoorsTileEntity;
import com.swdteam.common.tileentity.tardis.RoundelDoorTileEntity;
import com.swdteam.network.packets.PacketClickTardisDoors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin(PacketClickTardisDoors.class)
public class PacketClickTardisDoorsMixin {

    @Inject(at = @At(value = "HEAD", by = 5), remap = false, method = "handle")
    private static void handle(PacketClickTardisDoors msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo info) {
        ClickDetectorBlock.processAll(ctx.get().getSender().blockPosition(), ctx.get().getSender().level);
    }
}
