package com.frogrilla.dalek_mod_redstone.common.tileentity;

import com.frogrilla.dalek_mod_redstone.common.block.StattenheimPanelBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.DataModuleItem;
import com.swdteam.common.item.StattenheimRemoteItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StattenheimPanelTile extends TileEntity implements IInventory {
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            if(hasData()) return data.getCapability(cap, side);
            else if(hasRemote()) return remote.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
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

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return !hasData();
    }

    @Override
    public ItemStack getItem(int slot) {
        return getData();
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack d_remove = getData();
        removeData();
        updateBlock();
        return d_remove;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        removeData();
        updateBlock();
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        Item item = itemStack.getItem();
        if(item instanceof DataModuleItem){
            setData(itemStack);
            updateBlock();
        }

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (level.getBlockEntity(worldPosition) != this) return false;
        return !(player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clearContent() {
        removeData();
        updateBlock();
    }

    public void updateBlock(){
        getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(StattenheimPanelBlock.REMOTE, hasRemote()).setValue(StattenheimPanelBlock.DATA, StattenheimPanelTile.dataType(getData())));
    }
}
