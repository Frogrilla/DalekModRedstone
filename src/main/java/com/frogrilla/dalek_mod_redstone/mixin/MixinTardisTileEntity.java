package com.frogrilla.dalek_mod_redstone.mixin;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMFlightMode;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.Location;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.teleport.TeleportRequest;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.util.SWDMathUtils;
import com.swdteam.util.TeleportUtil;
import com.swdteam.util.math.Position;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(TardisTileEntity.class)
public class MixinTardisTileEntity {
    @Inject(at = @At(value = "HEAD"), method = "tick", remap = false, cancellable = true)
    private void tick(CallbackInfo info){
        info.cancel();

        TardisTileEntity t = (TardisTileEntity)(Object)this;

        if (t.getLevel().isClientSide && t.getLevel().random.nextInt(100) == 50) {
            t.snowCheck();
        }

        doorAnimation(t);
        long tickTime = System.currentTimeMillis() - t.lastTickTime;
        t.lastTickTime = System.currentTimeMillis();
        if (t.state == TardisState.DEMAT) {
            demat = true;
            if (t.animStartTime == 0L) {
                t.animStartTime = System.currentTimeMillis();
            }

            if (tickTime > 100L) {
                t.animStartTime += tickTime;
            }

            t.dematTime = (float)((double)(System.currentTimeMillis() - t.animStartTime) / 10000.0);
            if (t.dematTime >= 1.0F) {
                t.dematTime = 1.0F;
            }

            if (t.dematTime == 1.0F) {
                t.getLevel().setBlockAndUpdate(t.getBlockPos(), Blocks.AIR.defaultBlockState());
                t.animStartTime = 0L;
            }
        } else if (t.state == TardisState.REMAT) {
            demat = false;
            if (t.animStartTime == 0L) {
                t.animStartTime = System.currentTimeMillis();
            }

            if (tickTime > 100L) {
                t.animStartTime += tickTime;
            }

            if (System.currentTimeMillis() - t.animStartTime > 9000L) {
                t.dematTime = 1.0F - (float)((double)(System.currentTimeMillis() - (t.animStartTime + 9000L)) / 10000.0);
            }

            if (t.dematTime <= 0.0F) {
                t.dematTime = 0.0F;
            }

            if (t.dematTime == 0.0F) {
                t.setState(TardisState.NEUTRAL);
                t.getLevel().updateNeighborsAt(t.getBlockPos(), t.getBlockState().getBlock());
                t.animStartTime = 0L;
            }
        }

        t.pulses = 1.0F - t.dematTime + MathHelper.cos(t.dematTime * 3.141592F * 10.0F) * 0.25F * MathHelper.sin(t.dematTime * 3.141592F);
        if (t.getLevel().getBlockState(t.getBlockPos().offset(0, -1, 0)).getMaterial() == Material.AIR) {
            ++t.bobTime;
            ++t.rotation;
        } else {
            t.bobTime = 0;
            t.rotation = SWDMathUtils.SnapRotationToCardinal(t.rotation);
        }

        if (!t.getLevel().isClientSide) {
            t.tardisData = DMTardis.getTardis(t.globalID);
            if (t.tardisData != null && (t.doorOpenLeft || t.doorOpenRight)) {
                TardisTileEntity.defaultAABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
                AxisAlignedBB bounds = TardisTileEntity.defaultAABB.move(t.getBlockPos()).inflate(-0.30000001192092896, 0.0, -0.30000001192092896);
                bounds = bounds.move(Math.sin(Math.toRadians((double)t.rotation)) * 0.5, 0.0, -Math.cos(Math.toRadians((double)t.rotation)) * 0.5);
                List<Entity> entities = t.getLevel().getEntitiesOfClass(Entity.class, bounds);
                Predicate<Entity> inFlight = (entity) -> {
                    return entity instanceof PlayerEntity && DMFlightMode.isInFlight((PlayerEntity)entity);
                };
                Predicate<Entity> isRiding = (entity) -> {
                    return entity.isPassenger();
                };
                entities.removeIf(inFlight);
                entities.removeIf(isRiding);
                if (!entities.isEmpty()) {
                    Entity e = (Entity)entities.get(0);
                    Position vec = t.tardisData.getInteriorSpawnPosition();
                    if (!TeleportUtil.TELEPORT_REQUESTS.containsKey(e) && vec != null) {
                        Location loc = new Location(new Vector3d(vec.x(), vec.y(), vec.z()), DMDimensions.TARDIS);
                        loc.setFacing(t.tardisData.getInteriorSpawnRotation() + e.getYHeadRot() - t.rotation);
                        TeleportUtil.TELEPORT_REQUESTS.put(e, new TeleportRequest(loc));
                    }
                }
            }
        }
    }

    private void doorAnimation(TardisTileEntity t) {
        if (t.doorOpenLeft) {
            if (t.doorLeftRotation < 1.0F) {
                t.doorLeftRotation = (float)((double)t.doorLeftRotation + (0.10000000149011612 - (double)t.doorLeftRotation * 0.08));
            } else {
                t.doorLeftRotation = 1.0F;
            }
        } else if (t.doorLeftRotation > 0.0F) {
            if (t.doorLeftRotation - 0.3F < 0.0F) {
                t.doorLeftRotation = 0.0F;
            } else {
                t.doorLeftRotation -= 0.3F;
            }
        } else {
            t.doorLeftRotation = 0.0F;
        }

        if (t.doorOpenRight) {
            if (t.doorRightRotation < 1.0F) {
                t.doorRightRotation = (float)((double)t.doorRightRotation + (0.10000000149011612 - (double)t.doorRightRotation * 0.08));
            } else {
                t.doorRightRotation = 1.0F;
            }
        } else if (t.doorRightRotation > 0.0F) {
            if (t.doorRightRotation - 0.3F < 0.0F) {
                t.doorRightRotation = 0.0F;
            } else {
                t.doorRightRotation -= 0.3F;
            }
        } else {
            t.doorRightRotation = 0.0F;
        }

    }

    @Shadow
    protected boolean demat;
}
