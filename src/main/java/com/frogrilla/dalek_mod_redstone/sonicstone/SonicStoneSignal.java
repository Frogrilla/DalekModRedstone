package com.frogrilla.dalek_mod_redstone.sonicstone;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicStoneSignal {
    public final World world;
    public final BlockPos blockPos;
    public final Direction direction;
    public final int strength;
    public int distance = 0;
    public int tickCounter;

    public SonicStoneSignal(World world, BlockPos blockPos, Direction direction, int strength){
        this.world = world;
        this.blockPos = blockPos;
        this.direction = direction;
        this.strength = strength;
        this.tickCounter = strength*ISonicStone.TICKS_PER_BLOCK;
    }
}
