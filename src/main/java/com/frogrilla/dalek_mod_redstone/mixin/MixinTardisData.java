package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.block.RemoteLockBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.tardis.TardisData;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TardisData.class)
public class MixinTardisData {
    @Inject(at = @At(value = "RETURN"), method = "setLocked", remap = false, cancellable = true)
    private void setLocked(boolean locked, CallbackInfo info){
        TardisData data = (TardisData) (Object) this;
        RemoteLockTile.keyTiles.forEach(position -> {
            TileEntity tile = Minecraft.getInstance().level.getBlockEntity(position.toBlockPos());
            if(tile != null && tile.getType() == ModTileEntities.REMOTE_LOCK_TILE.get()){
                RemoteLockTile lock = (RemoteLockTile) tile;
                if(lock.getLinkedID() == data.getGlobalID()){
                    // updates the blockstates and models, but doesn't update neighbouring blocks
                    Minecraft.getInstance().level.setBlockAndUpdate(lock.getBlockPos(), lock.getBlockState().setValue(RemoteLockBlock.LOCKED, locked));
                }
            }
            else{
                RemoteLockTile.keyTiles.remove(position);
                System.out.printf("Removed position: %s%n", position);
            }
        });
    }
}