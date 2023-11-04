package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.Tardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.util.math.Position;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.security.Key;
import java.util.*;

public class RemoteLockBlock extends HorizontalBlock {
    public RemoteLockBlock(Properties builder) { super(builder); }
    public static List<Position> tilePositions = new ArrayList<>();
    private boolean allowInteract = true;
    private boolean powered = false;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    public static final BooleanProperty HAS_KEY = BooleanProperty.create("key");
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(HAS_KEY);
        builder.add(LOCKED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(HAS_KEY, false)
                .setValue(LOCKED, false);
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
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        if(!state.getValue(HAS_KEY)) return 0;
        if(state.getValue(LOCKED)) return 15;
        return 1;
    }
    @Override
    public void destroy(IWorld iWorld, BlockPos pos, BlockState state) {
        tilePositions.remove(pos);
        super.destroy(iWorld, pos, state);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

        if(!world.isClientSide && allowInteract && !powered && world.dimension() == DMDimensions.TARDIS){
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof RemoteLockTile){
                RemoteLockTile lockTile = (RemoteLockTile)tileEntity;
                if(lockTile.hasKey() && hand == Hand.MAIN_HAND && player.getItemInHand(hand).isEmpty()){
                    // take key
                    player.setItemInHand(hand, lockTile.getKey());
                    lockTile.removeKey();
                    BlockState new_state = state.setValue(HAS_KEY, false);
                    new_state = new_state.setValue(LOCKED, false);
                    world.setBlockAndUpdate(pos, new_state);
                }
                else if(!lockTile.hasKey() && lockTile.isKey(player.getItemInHand(hand).getItem())){
                    // insert key
                    int id = player.getItemInHand(hand).getTag().getInt("LinkedID");
                    TardisData t = DMTardis.getTardis(DMTardis.getIDForXZ(pos.getX(), pos.getZ()));
                    if(id == t.getGlobalID()){
                        lockTile.setKey(player.getItemInHand(hand));
                        System.out.println(lockTile.getLinkedID());
                        player.setItemInHand(hand, ItemStack.EMPTY);
                        world.setBlockAndUpdate(pos, state.setValue(HAS_KEY, true).setValue(LOCKED, t.isLocked()));
                        tilePositions.add(new Position(pos.getX(), pos.getY(), pos.getZ()));
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
            if(powered != nPower && nPower) turn_key(state, world, blockPos); // calls RemoteLockTile.updateAllTiles
            powered = nPower;
        }
    }

    public void turn_key(BlockState state, World world, BlockPos blockPos){
        if(world.dimension() != DMDimensions.TARDIS) return;
        RemoteLockTile lock = (RemoteLockTile)world.getBlockEntity(blockPos);
        if(lock.hasKey()){
            TardisData data = DMTardis.getTardis(DMTardis.getIDForXZ(blockPos.getX(), blockPos.getZ()));
            BlockPos pos = data.getCurrentLocation().getBlockPosition();
            ServerWorld serverWorld = world.getServer().getLevel(data.getCurrentLocation().dimensionWorldKey());
            if (serverWorld != null) {
                TileEntity t_entity = serverWorld.getBlockEntity(pos);
                TardisTileEntity tardis = (TardisTileEntity) t_entity;

                boolean locked = data.isLocked();
                data.setLocked(!locked);
                world.setBlockAndUpdate(blockPos, state.setValue(LOCKED, !locked));
                RemoteLockTile.updateAllTiles(data.getGlobalID(), !locked, world);

                if (!locked && tardis.doorOpenLeft || tardis.doorOpenRight){
                    tardis.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.INTERIOR);
                    tardis.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
                }

                world.playSound((PlayerEntity) null, blockPos, tardis.getCloseSound(), SoundCategory.BLOCKS, 0.5F, 1.0F);
            }
        }
    }
}
