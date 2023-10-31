package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.swdteam.common.init.DMItems;
import com.swdteam.util.math.Position;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class RemoteLockTile extends TileEntity {
    public static ArrayList<Position> keyTiles = new ArrayList<>();
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
            updateID();
            BlockPos blockPos = getBlockPos();
            Position pos = new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if(!keyTiles.contains(pos)) keyTiles.add(pos);
        }
    }
    public void removeKey() {
        this.heldKey = ItemStack.EMPTY;
        BlockPos blockPos = getBlockPos();
        Position pos = new Position(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        keyTiles.remove(pos);
    }

    public void updateID(){
        if(this.hasKey()){
            if(this.getKey().getTag() != null){
                linkedID = this.getKey().getTag().getInt("linkedID");
            }
        }
    }

    public int getLinkedID(){
        if(this.hasKey()){
            return this.linkedID;
        }
        return -1;
        /* When this stuff is done by the mixin, this.hasKey returns false, and it returns -1. Even if this.hasKey()
        returned true, it would still return the default value of linkedID. */
    }
}
