package com.frogrilla.dalek_mod_redstone.sonicstone;

import com.swdteam.common.init.DMSonicRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface ISonicStone {
    List<SonicStoneSignal> SONIC_STONE_SIGNALS = new ArrayList<>();
    List<SonicStoneSignal> SIGNAL_BUFFER = new ArrayList<>();
    BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    BooleanProperty BOOST = BooleanProperty.create("boost");
    int STRENGTH = 16;
    int DELAY_TIME = 16;
    int TICKS_PER_BLOCK = 4;

    void Signal(SonicStoneInteraction interaction);
    boolean DisruptSignal(SonicStoneInteraction interaction);

    static void CreateSignal(World world, BlockPos pos, int strength, Direction direction) {
        if(!world.isClientSide) SIGNAL_BUFFER.add(new SonicStoneSignal(world, pos, direction, strength));
    }

    static void SonicBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (DMSonicRegistry.SONIC_LOOKUP.containsKey(state.getBlock())) {
            DMSonicRegistry.ISonicInteraction son = (DMSonicRegistry.ISonicInteraction) DMSonicRegistry.SONIC_LOOKUP.get(state.getBlock());
            if (son != null) son.interact(world, null, null, pos);
        }
    }
}
