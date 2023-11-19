package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.block.RemoteLockBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.util.math.Position;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.Console;
import java.util.ArrayList;

public class RemoteLockTile extends TileEntity {
    private ItemStack heldKey = ItemStack.EMPTY;
    public RemoteLockTile(TileEntityType<?> tileEntityType) { super(tileEntityType); }
    public RemoteLockTile() { this(ModTileEntities.REMOTE_LOCK_TILE.get()); }
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        if(nbt.contains("Item")){
            this.heldKey = ItemStack.of(nbt.getCompound("Item"));
        }
        super.load(state, nbt);
    }
    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if(this.heldKey != null){
            CompoundNBT tag = new CompoundNBT();
            this.heldKey.save(tag);
            nbt.put("Item", tag);
        }
        else{
            nbt.remove("Item");
        }
        return super.save(nbt);
    }


    public boolean hasKey(){
        return heldKey != ItemStack.EMPTY;
    }

    public boolean isKey(Item item){
        return
            item == DMItems.TARDIS_KEY.get() ||
            item == DMItems.TARDIS_FAN_KEY.get() ||
            item == DMItems.TARDIS_SPADE_KEY.get() ||
            item == DMItems.TARDIS_LOCK_PICK_KEY.get();
    }

    public ItemStack getKey(){
        return this.heldKey;
    }

    public void setKey(ItemStack item){
        if(isKey(item.getItem())) {
            this.heldKey = item;
        }
    }
    public void removeKey() {
        this.heldKey = ItemStack.EMPTY;
    }
    public int getLinkedID(){
        if(this.hasKey()){
            if(this.getKey().getTag() != null){
                return this.getKey().getTag().getInt("linkedID");
            }
        }
        return -1;
        /* When this stuff is done by the mixin, this.hasKey returns false, and it returns -1. Even if this.hasKey()
        returned true, it would still return the default value of linkedID. */
    }
}
