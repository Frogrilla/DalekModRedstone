package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class StattenheimBlocktile extends TileEntity {

    public StattenheimBlocktile(TileEntityType<?> tileEntityType) { super(tileEntityType); }
    public StattenheimBlocktile() { this(ModTileEntities.STATTENHEIM_BLOCK_TILE.get()); }
}
