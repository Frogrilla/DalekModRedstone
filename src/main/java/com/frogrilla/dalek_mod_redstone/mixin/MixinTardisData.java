package com.frogrilla.dalek_mod_redstone.mixin;

import com.frogrilla.dalek_mod_redstone.common.block.RemoteLockBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.init.DMDimensions;
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
        //RemoteLockTile.updateAllTiles(data.getGlobalID(), locked, Minecraft.getInstance().level.getServer().getLevel(DMDimensions.TARDIS));
    }
}