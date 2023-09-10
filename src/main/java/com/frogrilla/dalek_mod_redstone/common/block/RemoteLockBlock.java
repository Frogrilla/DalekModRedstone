package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModTileEntities;
import com.frogrilla.dalek_mod_redstone.common.tileentity.RemoteLockTile;
import com.swdteam.common.init.DMItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class RemoteLockBlock extends HorizontalBlock {
    public RemoteLockBlock(Properties builder) { super(builder); }
    private boolean allowInteract = true;
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

        if(!world.isClientSide && allowInteract){
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
                    key.setKey(player.getItemInHand(hand));
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
                else{
                    // can't give or remove
                    return ActionResultType.FAIL;
                }

                allowInteract = false;
                world.getBlockTicks().scheduleTick(pos, state.getBlock(), 5);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void tick(BlockState p_225534_1_, ServerWorld p_225534_2_, BlockPos p_225534_3_, Random p_225534_4_) {
        if(!allowInteract) allowInteract = true;
        super.tick(p_225534_1_, p_225534_2_, p_225534_3_, p_225534_4_);
    }

//    @Override
//    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
//        Direction n = Direction.NORTH; Direction e = Direction.EAST; Direction s = Direction.SOUTH; Direction w = Direction.WEST;
//
//        BlockPos checkN = pos.offset(n.getStepX(), 0, n.getStepZ());
//        BlockPos checkE = pos.offset(e.getStepX(), 0, e.getStepZ());
//        BlockPos checkS = pos.offset(s.getStepX(), 0, s.getStepZ());
//        BlockPos checkW = pos.offset(w.getStepX(), 0, w.getStepZ());
//
//        boolean powered = world.getSignal(checkN, n) > 0 || world.getSignal(checkE, e) > 0 || world.getSignal(checkS, s) > 0 || world.getSignal(checkW, w) > 0;
//        if(powered){
//            System.out.println("remote_lock powered");
//        }
//    }
}
