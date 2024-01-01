package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.block.SonicBarrierBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.frogrilla.dalek_mod_redstone.common.tileentity.StattenheimPanelTile;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
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

public class SonicDirectorBlock extends SonicRelayBlock {
    public SonicDirectorBlock(Properties builder) {
        super(builder);
    }

    @Override
    public void onActivate(World world, BlockPos pos, BlockState state) {
        BlockPos under = pos.relative(state.getValue(FACING).getOpposite());
        BlockState underState = world.getBlockState(under);
        if (!(underState.getBlock() instanceof SonicStoneBlock)) {
            sonicBlock(world, under, underState);
        }
        sendSignal(world, state, pos, state.getValue(FACING), SEARCH_DISTANCE);
        world.getBlockTicks().scheduleTick(pos, this, 1);
    }
}
