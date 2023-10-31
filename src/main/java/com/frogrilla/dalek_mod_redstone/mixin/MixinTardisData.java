package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.block.RemoteLockBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.tardis.TardisData;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TardisData.class)
public class MixinTardisData {
    @Inject(at = @At(value = "RETURN"), method = "setLocked", remap = false, cancellable = true)
    private void setLocked(boolean locked, CallbackInfo info){
        TardisData data = (TardisData) (Object) this;
        // positions are added when a key is inserted into a tile, and removed when you take the key
        RemoteLockBlock.tilePositions.forEach(position -> {
            System.out.printf("Checking position: %s%n", position);
            TileEntity tile = Minecraft.getInstance().level.getBlockEntity(position.toBlockPos());
            if(tile != null && tile.getType() == ModTileEntities.REMOTE_LOCK_TILE.get()){
                RemoteLockTile lock = (RemoteLockTile) tile;
                System.out.printf("Tile at position: %s%n", position);
                System.out.printf("ID: %d%n", lock.getLinkedID());
                // always getting -1 (the default value for RemoteLockTile.linkedID)
                if(lock.getLinkedID() == data.getGlobalID()){
                    System.out.printf("Updating position: %s%n", position);
                    // updates the blockstates and models, but doesn't update neighbouring blocks
                    Minecraft.getInstance().level.setBlockAndUpdate(lock.getBlockPos(), lock.getBlockState().setValue(RemoteLockBlock.LOCKED, locked));
                    System.out.printf("Updated position: %s%n", position);
                }
            }
            else{
                RemoteLockBlock.tilePositions.remove(position);
                System.out.printf("Removed position: %s%n", position);
            }
        });
    }
}

// I have a horrible feeling this mess is something to do with the client and server