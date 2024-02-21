package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;


import com.frogrilla.dalek_mod_redstone.sonicstone.ISonicStone;
import com.frogrilla.dalek_mod_redstone.sonicstone.SonicStoneInteraction;
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
                        .setValue(ACTIVATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
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
    public void Signal(SonicStoneInteraction interaction) {
        if(DisruptSignal(interaction)) return;
        BlockState state = interaction.world.getBlockState(interaction.blockPos);
        BlockStateProperties.FACING.getAllValues().forEach(directionValuePair -> {
            Direction dir = directionValuePair.value();
            if (dir.getAxis() == state.getValue(AXIS)) return;
            BlockPos check = interaction.blockPos.relative(dir);
            ISonicStone.SonicBlock(interaction.world, check);
        });
        interaction.world.setBlockAndUpdate(interaction.blockPos, state.setValue(ACTIVATED, true));
        interaction.world.getBlockTicks().scheduleTick(interaction.blockPos, this, DELAY_TIME);
    }

    @Override
    public boolean DisruptSignal(SonicStoneInteraction interaction) {
        return interaction.direction.getAxis() != interaction.world.getBlockState(interaction.blockPos).getValue(AXIS);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false));
    }
}