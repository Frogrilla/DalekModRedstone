package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Dimension;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.security.Key;
import java.util.Random;

public class RemoteLockBlock extends HorizontalBlock {
    public RemoteLockBlock(Properties builder) { super(builder); }
    private boolean allowInteract = true;
    private boolean powered = false;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.REMOTE_LOCK_TILE.get().create();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if(!world.isClientSide && allowInteract && !powered && world.dimension() == DMDimensions.TARDIS){
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof RemoteLockTile){
                RemoteLockTile key = (RemoteLockTile)tileEntity;

                if(key.hasKey() && player.getItemInHand(hand).isEmpty()){
                    // take key
                    player.setItemInHand(hand, key.getKey());
                    key.setKey(ItemStack.EMPTY);
                }
                else if(!key.hasKey() && key.isKey(player.getItemInHand(hand).getItem())){
                    // insert key
                    int id = player.getItemInHand(hand).getTag().getInt("LinkedID");
                    TardisData t = DMTardis.getTardis(DMTardis.getIDForXZ(pos.getX(), pos.getZ()));
                    if(id == t.getGlobalID()){
                        key.setKey(player.getItemInHand(hand));
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                    else{
                        return ActionResultType.FAIL;
                    }
                }
                else{
                    // can't give or remove
                    return ActionResultType.FAIL;
                }

                allowInteract = false;
                world.getBlockTicks().scheduleTick(pos, state.getBlock(), 4);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void tick(BlockState blockState, ServerWorld world, BlockPos blockPos, Random random) {
        allowInteract = true;
        super.tick(blockState, world, blockPos, random);
    }

   public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);

            if(powered != nPower && nPower && world.dimension() == DMDimensions.TARDIS){
                RemoteLockTile key = (RemoteLockTile)world.getBlockEntity(blockPos);
                TardisData t = DMTardis.getTardis(DMTardis.getIDForXZ(blockPos.getX(), blockPos.getZ()));

                if(key.hasKey()){
                    t.setLocked(!t.isLocked());
                }

            }

            powered = nPower;
        }
    }
}
