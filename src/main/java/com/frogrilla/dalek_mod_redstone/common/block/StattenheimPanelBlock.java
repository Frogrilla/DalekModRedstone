package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
import com.swdteam.common.init.*;
import com.swdteam.common.item.DataModuleItem;
import com.swdteam.common.item.StattenheimRemoteItem;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.util.ChatUtil;
import com.swdteam.util.WorldUtils;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class StattenheimPanelBlock extends HorizontalBlock {
    private boolean powered = false;

    private static final BooleanProperty REMOTE = BooleanProperty.create("remote");
    private static final IntegerProperty DATA = IntegerProperty.create("data", 0,2);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    public StattenheimPanelBlock(Properties builder) { super(builder); }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(REMOTE);
        builder.add(DATA);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(REMOTE, false)
                .setValue(DATA, 0);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.STATTENHEIM_BLOCK_TILE.get().create();
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND){
            TileEntity t = world.getBlockEntity(blockPos);
            if(!(t instanceof StattenheimPanelTile)) return ActionResultType.FAIL;
            StattenheimPanelTile tile = (StattenheimPanelTile) t;
            if(player.getItemInHand(hand) == ItemStack.EMPTY){
                // take data
                if(tile.hasData() && !(player.isShiftKeyDown() && tile.hasRemote())){
                    player.setItemInHand(hand, tile.getData());
                    tile.removeData();
                    world.setBlockAndUpdate(blockPos, blockState.setValue(DATA, 0));
                    return ActionResultType.SUCCESS;
                // take remote
                } else if (tile.hasRemote()) {
                    player.setItemInHand(hand, tile.getRemote());
                    tile.removeRemote();
                    world.setBlockAndUpdate(blockPos, blockState.setValue(REMOTE, false));
                    return ActionResultType.SUCCESS;
                }
            }
            else if(StattenheimPanelTile.isRemote(player.getItemInHand(hand))){
                // give remote
                if(tile.hasRemote()) return ActionResultType.FAIL;
                tile.setRemote(player.getItemInHand(hand));
                player.setItemInHand(hand, ItemStack.EMPTY);
                world.setBlockAndUpdate(blockPos, blockState.setValue(REMOTE, true));
                return ActionResultType.CONSUME;
            }
            else if(StattenheimPanelTile.dataType(player.getItemInHand(hand)) > 0){
                // give data
                if(tile.hasData()) return ActionResultType.FAIL;
                tile.setData(player.getItemInHand(hand));
                player.getItemInHand(hand).shrink(1);
                if(player.getItemInHand(hand).getCount() == 0) player.setItemInHand(hand, ItemStack.EMPTY);
                world.setBlockAndUpdate(blockPos, blockState.setValue(DATA, StattenheimPanelTile.dataType(tile.getData())));
                return ActionResultType.CONSUME;
            }
        }

        return ActionResultType.FAIL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        int i = 0;
        if (state.getValue(REMOTE)) i += 13;
        i += state.getValue(DATA);
        return i;
    }

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);

            if(powered != nPower && nPower){
                StattenheimPanelTile tile = (StattenheimPanelTile)world.getBlockEntity(blockPos);
                StattenheimRemoteItem remote = (StattenheimRemoteItem)tile.getRemote().getItem();
                DataModuleItem data = (DataModuleItem)tile.getData().getItem();
                sendTardis(tile.getRemote(), world, tile.getData());
            }

            powered = nPower;
        }
    }

    static void sendTardis(ItemStack remote, World world, ItemStack module){
        ListNBT list = module.getTag().getList("location", 10);
        CompoundNBT tag = list.getCompound(0);
        BlockPos pos = new BlockPos(tag.getInt("pos_x"),tag.getInt("pos_y"),tag.getInt("pos_z"));

        if(!world.isEmptyBlock(pos)) return;

        int tardisID = remote.getTag().getInt(DMNBTKeys.LINKED_ID);
        TardisData data = DMTardis.getTardis(tardisID);
        MinecraftServer server = world.getServer();
        ServerWorld tardisDim = server.getLevel(DMDimensions.TARDIS);

        if (tardisDim.isLoaded(data.getInteriorSpawnPosition().toBlockPos())) {
            tardisDim.playSound((PlayerEntity)null, data.getInteriorSpawnPosition().x(), data.getInteriorSpawnPosition().y(), data.getInteriorSpawnPosition().z(), (SoundEvent) DMSoundEvents.TARDIS_REMAT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        if (!data.isInFlight()) {
            world.playSound((PlayerEntity)null, pos, (SoundEvent)DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SUMMON.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
            BlockPos currentPos = data.getCurrentLocation().getBlockPosition();
            ServerWorld serverWorld = server.getLevel(data.getCurrentLocation().dimensionWorldKey());
            TileEntity te = serverWorld.getBlockEntity(currentPos);
            if (te instanceof TardisTileEntity) {
                if (TardisActionList.doAnimation(serverWorld, currentPos)) {
                    ((TardisTileEntity)te).setState(TardisState.DEMAT);
                } else {
                    serverWorld.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        world.setBlockAndUpdate(pos, (BlockState)((Block)DMBlocks.TARDIS.get()).defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() instanceof FlowingFluidBlock));
        data.setPreviousLocation(data.getCurrentLocation());
        data.setCurrentLocation(pos, world.dimension());
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof TardisTileEntity) {
            TardisTileEntity tardis = (TardisTileEntity)te;
            tardis.globalID = tardisID;
            tardis.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
            tardis.rotation = tag.getFloat("facing");
            tardis.setState(TardisState.REMAT);
            data.getCurrentLocation().setFacing(tardis.rotation);
        }

        data.save();
    }
}
