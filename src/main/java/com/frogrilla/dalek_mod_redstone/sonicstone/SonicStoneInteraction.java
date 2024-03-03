package com.frogrilla.dalek_mod_redstone.sonicstone;

import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicStoneInteraction {
    public final World world;
    public final BlockPos blockPos;
    public final Direction direction;
    public final int distance;
    public final int strength;

    public SonicStoneInteraction(World world, BlockPos blockPos, Direction dir, int strength, int distance){
        this.blockPos = blockPos;
        this.world = world;
        this.direction = dir;
        this.strength = strength;
        this.distance = distance;
    }
}
