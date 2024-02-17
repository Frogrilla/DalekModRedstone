package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicGateBlock extends Block implements ISonicStone {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public SonicGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(AXIS, Direction.Axis.Y)
                        .setValue(DELAY, 0)
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        builder.add(DELAY);
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
                .setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public boolean Signal(World world, BlockPos pos, int strength, Direction direction, int distance) {
        if(world.getBlockState(pos).getValue(ACTIVATED) || direction.getAxis() != world.getBlockState(pos).getValue(AXIS)) return true;
        world.getBlockTicks().scheduleTick(pos, world.getBlockState(pos).getBlock(), distance);
        return false;
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
        BlockStateProperties.FACING.getAllValues().forEach(directionValuePair -> {
            Direction dir = directionValuePair.value();
            if(dir.getAxis() == state.getValue(AXIS)) return;
            BlockPos check = pos.relative(dir);
            ISonicStone.SonicBlock(world, check);
        });
        world.setBlockAndUpdate(pos, state.setValue(DELAY, DELAY_TIME).setValue(ACTIVATED, true));
        world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
    }
}
