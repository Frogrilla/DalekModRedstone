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
import net.minecraftforge.fml.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin(PacketClickTardisDoors.class)
public class MixinPacketClickTardisDoors {

    @Inject(at = @At(value = "HEAD"), method = "handle", remap = false, cancellable = true)
    private static void handle(PacketClickTardisDoors msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo info) {
        info.cancel();
        ((NetworkEvent.Context) ctx.get()).enqueueWork(() -> {
            if (((NetworkEvent.Context) ctx.get()).getNetworkManager().getDirection() == PacketDirection.SERVERBOUND) {
                PlayerEntity player = ((NetworkEvent.Context) ctx.get()).getSender();
                if (player.getItemInHand(Hand.MAIN_HAND).isEmpty() || player.getItemInHand(Hand.OFF_HAND).isEmpty()) {
                    player.level.playSound((PlayerEntity) null, player.blockPosition(), (SoundEvent) DMSoundEvents.PLAYER_SNAP.get(), SoundCategory.PLAYERS, 0.4F + player.level.random.nextFloat() / 3.0F, 0.9F + player.level.random.nextFloat() / 10.0F);
                    ClickDetectorBlock.processAll(player.blockPosition(), player.level);
                    List<TileEntity> tiles = player.level.tickableBlockEntities;

                    for (int i = 0; i < tiles.size(); ++i) {
                        TileEntity tile = (TileEntity) tiles.get(i);
                        Vector3d playerPos;
                        Vector3d tardisPos;
                        double distance;
                        if (tile.getType() == DMBlockEntities.TILE_TARDIS.get()) {
                            playerPos = player.position();
                            tardisPos = new Vector3d((double) tile.getBlockPos().getX(), (double) tile.getBlockPos().getY(), (double) tile.getBlockPos().getZ());
                            distance = playerPos.distanceTo(tardisPos);
                            if (distance <= 15.0) {
                                TardisData data = ((TardisTileEntity) tile).tardisData;
                                if (data != null && !data.isLocked()) {
                                    TardisTileEntity tardisx = (TardisTileEntity) tile;
                                    if (tardisx.state == TardisState.NEUTRAL) {
                                        if (!tardisx.doorOpenLeft && !tardisx.doorOpenRight) {
                                            tardisx.toggleDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
                                        } else {
                                            tardisx.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
                                        }
                                    }
                                }
                            }
                        } else {
                            TardisData datax;
                            if (tile instanceof DoubleDoorsTileEntity) {
                                playerPos = player.position();
                                tardisPos = new Vector3d((double) tile.getBlockPos().getX(), (double) tile.getBlockPos().getY(), (double) tile.getBlockPos().getZ());
                                distance = playerPos.distanceTo(tardisPos);
                                if (distance <= 15.0) {
                                    DoubleDoorsTileEntity tardis = (DoubleDoorsTileEntity) tile;
                                    datax = DMTardis.getTardisFromInteriorPos(tile.getBlockPos());
                                    if (tardis.isMainDoor() && datax != null && !datax.isLocked() && !datax.isInFlight()) {
                                        if (!tardis.isOpen(TardisDoor.LEFT) && !tardis.isOpen(TardisDoor.RIGHT)) {
                                            tardis.setOpen(TardisDoor.BOTH, true);
                                        } else {
                                            tardis.setOpen(TardisDoor.BOTH, false);
                                        }
                                    }
                                }
                            } else if (tile instanceof RoundelDoorTileEntity) {
                                playerPos = player.position();
                                tardisPos = new Vector3d((double) tile.getBlockPos().getX(), (double) tile.getBlockPos().getY(), (double) tile.getBlockPos().getZ());
                                distance = playerPos.distanceTo(tardisPos);
                                if (distance <= 15.0) {
                                    RoundelDoorTileEntity tardisxx = (RoundelDoorTileEntity) tile;
                                    datax = DMTardis.getTardisFromInteriorPos(tile.getBlockPos());
                                    if (tardisxx.isMainDoor() && datax != null && !datax.isLocked() && !datax.isInFlight()) {
                                        if (tardisxx.isOpen()) {
                                            tardisxx.setOpen(false);
                                        } else {
                                            tardisxx.setOpen(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);

    }
}
