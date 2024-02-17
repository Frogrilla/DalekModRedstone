package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicBoosterBlock extends Block implements ISonicStone {
    public SonicBoosterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(DIR_IN, Direction.DOWN)
                        .setValue(DELAY, 0)
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DIR_IN);
        builder.add(DELAY);
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean Signal(World world, BlockPos pos, int strength, Direction direction, int distance) {
        if(world.getBlockState(pos).getValue(ACTIVATED)) return true;
        world.setBlockAndUpdate(pos, world.getBlockState(pos).setValue(DIR_IN, direction));
        world.getBlockTicks().scheduleTick(pos, world.getBlockState(pos).getBlock(), distance);
        return true;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // DELAY TIMER
        if(state.getValue(ACTIVATED)){
            int cur = state.getValue(DELAY)-1;
            if(cur == 0) {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, 0).setValue(ACTIVATED, false));
            }
            else {
                world.setBlockAndUpdate(pos, state.setValue(DELAY, cur));
                world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
            }
            return;
        }

        // SONIC STONE PROCESS
        ISonicStone.SendSignal(world, pos, SEARCH_DISTANCE, world.getBlockState(pos).getValue(DIR_IN));
        world.setBlockAndUpdate(pos, state.setValue(DELAY, DELAY_TIME).setValue(ACTIVATED, true));
        world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
    }
}
