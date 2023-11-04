package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.DataModuleItem;
import com.swdteam.common.item.StattenheimRemoteItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

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
                if(tile.hasData()){
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

    public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block block, BlockPos blockPos1, boolean isMoving) {
        if (!world.isClientSide) {
            boolean nPower = world.hasNeighborSignal(blockPos);

            if(powered != nPower && nPower){
                StattenheimPanelTile tile = (StattenheimPanelTile)world.getBlockEntity(blockPos);
                StattenheimRemoteItem remote = (StattenheimRemoteItem)tile.getRemote().getItem();
                DataModuleItem data = (DataModuleItem)tile.getData().getItem();
                remote.useOn(new ItemUseContext((PlayerEntity) null, Hand.MAIN_HAND, new BlockRayTraceResult(Vector3d.ZERO, Direction.NORTH, blockPos.above(), true)));
            }

            powered = nPower;
        }
    }
}
