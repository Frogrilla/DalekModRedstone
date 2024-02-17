package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SonicRelayNew extends Block implements ISonicStone {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SonicRelayNew(Properties properties) {
        super(properties);
        this.registerDefaultState(
                getStateDefinition().any()
                        .setValue(FACING, Direction.UP)
                        .setValue(POWERED, false)
                        .setValue(DIR_IN, Direction.DOWN)
                        .setValue(DELAY, 0)
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(DIR_IN);
        builder.add(DELAY);
        builder.add(ACTIVATED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = defaultBlockState();
        return state
                .setValue(FACING, context.getClickedFace())
                .setValue(DIR_IN, context.getClickedFace().getOpposite());
    }

    @Override
    public boolean Signal(World world, BlockPos pos, int strength, Direction direction, int distance) {
        if(world.getBlockState(pos).getValue(ACTIVATED) || world.getBlockState(pos).getValue(POWERED)) return true;
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
        BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
        BlockState underState = world.getBlockState(under);
        ISonicStone.SonicBlock(world, under, underState);
        FACING.getAllValues().forEach(pair -> {
            Direction direction = pair.value();
            if(direction == state.getValue(DIR_IN)) return;
            if(direction == state.getValue(FACING).getOpposite()) return;
            ISonicStone.SendSignal(world, pos, SEARCH_DISTANCE, direction);
        });
        world.setBlockAndUpdate(pos, state.setValue(DIR_IN, state.getValue(FACING).getOpposite()).setValue(DELAY, DELAY_TIME).setValue(ACTIVATED, true));
        world.getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
    }
}
