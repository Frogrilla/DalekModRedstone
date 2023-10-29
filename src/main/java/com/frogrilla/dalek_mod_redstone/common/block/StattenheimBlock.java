package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimBlocktile;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.Tardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class StattenheimBlock extends HorizontalBlock {
    private boolean powered = false;
    public StattenheimBlock(Properties builder) { super(builder); }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.STATTENHEIM_BLOCK_TILE.get().create();
    }
    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);

            if(powered != nPower && nPower){
                StattenheimBlocktile tile = (StattenheimBlocktile)world.getBlockEntity(blockPos);
                int id = 0;
                TardisData tardis = DMTardis.getTardis(id);
            }

            powered = nPower;
        }
    }
}
