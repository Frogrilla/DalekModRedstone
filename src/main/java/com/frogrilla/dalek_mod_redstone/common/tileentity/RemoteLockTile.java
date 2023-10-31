package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.swdteam.common.init.DMItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class RemoteLockTile extends TileEntity {
    private ItemStack heldKey = ItemStack.EMPTY;
    private int linkedID = -1;
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
        return isKey(heldKey.getItem());
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
        if(!isKey(item.getItem())) return;
        this.heldKey = item;
    }

    public void setLinkedID(int id) { this.linkedID = id; };
    public int getLinkedID() { return this.linkedID; }

}
