package com.frogrilla.dalek_mod_redstone.sonicstone;

import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public interface ISonicStone {

    List<SonicStoneInteraction> SONIC_STONE_INTERACTIONS = new ArrayList<>();
    List<SonicStoneInteraction> INTERACTION_BUFFER = new ArrayList<>();
    BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    int SEARCH_DISTANCE = 15;
    int DELAY_TIME = 4;

    void Signal(SonicStoneInteraction interaction);
    boolean DisruptSignal(SonicStoneInteraction interaction);

    static boolean SendSignal(World world, BlockPos pos, int strength, Direction direction) {
        if(world.isClientSide) return false;
        for (int i = 1; i <= strength; ++i) {
            BlockPos checkPos = pos.relative(direction, i);
            BlockState checkState = world.getBlockState(checkPos);
            Block checkBlock = checkState.getBlock();

            if (checkBlock instanceof ISonicStone) {
                ISonicStone sonicBlock = (ISonicStone)checkBlock;
                SonicStoneInteraction interaction = new SonicStoneInteraction(checkPos, world, direction, strength, i, i*2);
                AddSonicInteraction(interaction);
                if(sonicBlock.DisruptSignal(interaction)) return true;
            }
        }
        return false;
    }

    static void SonicBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (DMSonicRegistry.SONIC_LOOKUP.containsKey(state.getBlock())) {
            DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction) DMSonicRegistry.SONIC_LOOKUP.get(state.getBlock());
            if (son != null) son.interact(world, null, null, pos);
        }
    }

    static void AddSonicInteraction(SonicStoneInteraction interaction){
        INTERACTION_BUFFER.add(interaction);
    }
}
