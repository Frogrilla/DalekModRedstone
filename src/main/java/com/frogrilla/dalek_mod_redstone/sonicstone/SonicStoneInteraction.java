package com.frogrilla.dalek_mod_redstone.sonicstone;

import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicStoneInteraction {
    public final BlockPos blockPos;
    public final World world;
    public final Direction direction;
    public final int distance;
    public final int strength;
    public int ticksLeft;

    public SonicStoneInteraction(BlockPos pos, World world, Direction dir, int strength, int distance, int ticksLeft){
        this.blockPos = pos;
        this.world = world;
        this.direction = dir;
        this.strength = strength;
        this.distance = distance;
        this.ticksLeft = ticksLeft;
    }
}
