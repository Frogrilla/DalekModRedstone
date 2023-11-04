package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.swdteam.common.init.DMItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class StattenheimPanelTile extends TileEntity {
    private ItemStack remote = ItemStack.EMPTY;
    private ItemStack data = ItemStack.EMPTY;
    public StattenheimPanelTile(TileEntityType<?> tileEntityType) { super(tileEntityType); }
    public StattenheimPanelTile() { this(ModTileEntities.STATTENHEIM_BLOCK_TILE.get()); }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        if(nbt.contains("remote")) this.remote = ItemStack.of(nbt.getCompound("remote"));
        if(nbt.contains("data")) this.data = ItemStack.of(nbt.getCompound("data"));
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        if(this.remote != ItemStack.EMPTY){
            CompoundNBT tag = new CompoundNBT();
            this.remote.save(tag);
            nbt.put("remote", tag);
        }
        else{
            nbt.remove("remote");
        }

        if(this.data != ItemStack.EMPTY){
            CompoundNBT tag = new CompoundNBT();
            this.data.save(tag);
            nbt.put("data", tag);
        }
        else{
            nbt.remove("data");
        }

        return super.save(nbt);
    }

    public ItemStack getRemote() { return remote; }
    public ItemStack getData() { return data; }
    public void setRemote(ItemStack remoteIn) { if(isRemote(remoteIn)) { remote = remoteIn.copy(); } }
    public void setData(ItemStack dataIn) {
        if(dataType(dataIn) > 0) {
            data = dataIn.copy();
            data.setCount(1);
        }
    }
    public void removeRemote() { remote = ItemStack.EMPTY; }
    public void removeData() { data = ItemStack.EMPTY; }
    public boolean hasRemote() { return !(remote == ItemStack.EMPTY); }
    public boolean hasData() { return !(data == ItemStack.EMPTY); }
    public static boolean isRemote(ItemStack item) { return item.getItem() == DMItems.STATTENHEIM_REMOTE.get(); }
    public static int dataType(ItemStack item){
        Item type = item.getItem();
        if(type == DMItems.DATA_MODULE.get()) return 1;
        if(type == DMItems.DATA_MODULE_GOLD.get()) return 2;
        return 0;
    }
}
