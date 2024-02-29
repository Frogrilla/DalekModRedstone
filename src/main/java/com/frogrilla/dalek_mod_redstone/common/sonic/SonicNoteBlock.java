package com.frogrilla.dalek_mod_redstone.common.sonic;

import com.frogrilla.dalek_mod_redstone.common.block.SonicResonatorBlock;
import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.SonicCategory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SonicNoteBlock implements DMSonicRegistry.ISonicInteraction {
    @Override
    public void interact(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        if (o instanceof BlockPos) {
            BlockPos p = (BlockPos) o;
            BlockState state = world.getBlockState(p);
            if(state.getBlock() == Blocks.NOTE_BLOCK){
                int _new = net.minecraftforge.common.ForgeHooks.onNoteChange(world, p, state, state.getValue(BlockStateProperties.NOTE), state.cycle(BlockStateProperties.NOTE).getValue(BlockStateProperties.NOTE));
                if (_new == -1) return;
                state = state.setValue(BlockStateProperties.NOTE, _new);
                world.setBlock(p, state, 3);
            }
        }
    }

    @Override
    public int scanTime() {
        return 5;
    }

    @Override
    public boolean disableDefaultInteraction(World world, PlayerEntity playerEntity, ItemStack itemStack, Object o) {
        return true;
    }

    @Override
    public SonicCategory getCategory() {
        return SonicCategory.REDSTONE;
    }
}
