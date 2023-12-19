package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
import com.swdteam.common.init.*;
import com.swdteam.common.item.DataModuleItem;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tileentity.TardisTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class StattenheimPanelBlock extends HorizontalBlock {
    public static final BooleanProperty REMOTE = BooleanProperty.create("remote");
    public static final IntegerProperty DATA = IntegerProperty.create("data", 0,2);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
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
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(REMOTE, false)
                .setValue(DATA, 0)
                .setValue(POWERED, false);
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

            if(state.getValue(POWERED) != nPower && nPower){
                StattenheimPanelTile tile = (StattenheimPanelTile)world.getBlockEntity(blockPos);
                if(tile.hasRemote()) {
                    if(tile.hasData()){
                        try {
                            sendTardis(tile.getRemote(), world, tile.getData());
                        }
                        catch (Exception e){
                            DalekModRedstone.LOGGER.warn("Something went wrong when sending the TARDIS with a stattenheim panel");
                        }
                    }
                }
            }

            world.setBlockAndUpdate(blockPos, state.setValue(POWERED, nPower));
        }
    }

    static void sendTardis(ItemStack remote, World world, ItemStack module){
        ListNBT list = module.getTag().getList("location", 10);
        CompoundNBT tag = list.getCompound(0);
        BlockPos pos = new BlockPos(tag.getInt("pos_x"),tag.getInt("pos_y"),tag.getInt("pos_z"));
        RegistryKey<World> dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("location")));
        world = world.getServer().getLevel(dimension);
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
        data.setCurrentLocation(pos, dimension);
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

    static void toggleTardis(ItemStack remote, World world){
        int tardisID = remote.getTag().getInt(DMNBTKeys.LINKED_ID);
        TardisData data = DMTardis.getTardis(tardisID);
        BlockPos pos = data.getCurrentLocation().getBlockPosition();

        if (data.isInFlight()) {
            if (data.timeLeft() == 0.0) {
                if (TardisActionList.remat(world.getPlayerByUUID(data.getOwner_uuid()), world, data)) {
                    world.playSound((PlayerEntity)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (SoundEvent)DMSoundEvents.TARDIS_REMAT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        } else if (TardisActionList.demat(world.getPlayerByUUID(data.getOwner_uuid()), world, data)) {
            world.playSound((PlayerEntity)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (SoundEvent)DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }
}
