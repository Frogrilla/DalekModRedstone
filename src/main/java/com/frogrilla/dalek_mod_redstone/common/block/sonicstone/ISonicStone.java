package com.frogrilla.dalek_mod_redstone.common.block.sonicstone;

import com.frogrilla.dalek_mod_redstone.DalekModRedstone;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISonicStone {

    DirectionProperty DIR_IN = DirectionProperty.create("dir_in", direction -> true);
    IntegerProperty STRENGTH_IN = IntegerProperty.create("strength_in", 0, 15);
    IntegerProperty DISTANCE_IN = IntegerProperty.create("distance_in", 0, 15);
    IntegerProperty DELAY = IntegerProperty.create("delay", 0, 20);
    BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    int SEARCH_DISTANCE = 15;
    int DELAY_TIME = 4;

    boolean Signal(World world, BlockPos pos, int strength, Direction direction, int distance);

    static boolean SendSignal(World world, BlockPos pos, int strength, Direction direction) {
        for(int i = 1; i <= strength; ++i){
            BlockPos checkPos = pos.relative(direction, i);
            BlockState checkState = world.getBlockState(checkPos);
            Block checkBlock = checkState.getBlock();

            if(checkBlock instanceof ISonicStone){
                boolean stop = ((ISonicStone) checkBlock).Signal(world, checkPos, strength, direction, i);
                if(stop) return true;
            }
        }
        return false;
    }

    static void SonicBlock(World world, BlockPos pos, BlockState state){
        if (DMSonicRegistry.SONIC_LOOKUP.containsKey(state.getBlock())) {
            DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction)DMSonicRegistry.SONIC_LOOKUP.get(state.getBlock());
            if(son != null) son.interact(world, null, null, pos);
        }
    }
}
