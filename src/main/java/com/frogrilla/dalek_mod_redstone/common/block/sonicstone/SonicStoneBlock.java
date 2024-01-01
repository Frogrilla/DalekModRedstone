package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.SonicBarrierBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class SonicStoneBlock extends Block {
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    public static final IntegerProperty DELAY = IntegerProperty.create("delay", 0, 8);
    public static final DirectionProperty RECEIVE_DIR = DirectionProperty.create("receive_dir", direction -> true);
    public static final int SEARCH_DISTANCE = 8;
    public static final int DELAY_TIME = 4;
    public SonicStoneBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(ACTIVATED, false)
                        .setValue(DELAY, 0)
                        .setValue(RECEIVE_DIR, Direction.DOWN)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
        builder.add(DELAY);
        builder.add(RECEIVE_DIR);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.getValue(ACTIVATED)){
            if(state.getValue(DELAY) == 1){
                onDeactivate(world, pos, state);
                world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false).setValue(DELAY, 0));
            }
            else {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, state.getValue(DELAY)-1));
                world.getBlockTicks().scheduleTick(pos, this, 1);
            }
        }
        else{
            onActivate(world, pos, state);
            world.setBlockAndUpdate(pos, state.setValue(DELAY, DELAY_TIME).setValue(ACTIVATED, true));
            world.getBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    public void onDeactivate(World world, BlockPos pos, BlockState state){

    }

    public void onActivate(World world, BlockPos pos, BlockState state){

    }

    public boolean sendSignal(World world, BlockState state, BlockPos pos, Direction dir, int search){
        for(int i = 1; i <= search; ++i){
            BlockPos checkPos = pos.relative(dir, i);
            BlockState checkState = world.getBlockState(checkPos);

            if(checkState.getBlock() == ModBlocks.SONIC_BARRIER.get() && !SonicBarrierBlock.getStateFromDirection(dir.getOpposite(), checkState)) return false;
            if(checkState.getBlock() instanceof SonicStoneBlock){
                if(checkState.getValue(DELAY) <= i){
                    world.setBlockAndUpdate(checkPos, checkState.setValue(RECEIVE_DIR, dir.getOpposite()));
                    world.getBlockTicks().scheduleTick(checkPos, checkState.getBlock(), i);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void sonicBlock(World world, BlockPos pos, BlockState state){
        if (DMSonicRegistry.SONIC_LOOKUP.containsKey(state.getBlock())) {
            DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction)DMSonicRegistry.SONIC_LOOKUP.get(state.getBlock());
            if(son != null) son.interact(world, null, null, pos);
        }
    }
}
