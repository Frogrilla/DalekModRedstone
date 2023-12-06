package com.frogrilla.dalek_mod_redstone.common.block;

import com.frogrilla.dalek_mod_redstone.common.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class ClickDetectorBlock extends HorizontalBlock{
    public ClickDetectorBlock(Properties builder) {
        super(builder);
    }
    static final IntegerProperty PULSE = IntegerProperty.create("pulse", 0, 3);
    static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
    static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    private static final VoxelShape SHAPE_N = VoxelShapes.join(Block.box(4, 1, 3, 14, 3, 13), Block.box(0, 0, 0, 16, 2, 16), IBooleanFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.join(Block.box(3, 1, 4, 13, 3, 14), Block.box(0, 0, 0, 16, 2, 16), IBooleanFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.join(Block.box(2, 1, 3, 12, 3, 13), Block.box(0, 0, 0, 16, 2, 16), IBooleanFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.join(Block.box(3, 1, 2, 13, 3, 12), Block.box(0, 0, 0, 16, 2, 16), IBooleanFunction.OR);

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        switch (state.getValue(FACING)){
            case NORTH:
                return SHAPE_N;
            case EAST:
                return SHAPE_E;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            default:
                return SHAPE_N;
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(PULSE);
        builder.add(ACTIVATED);
        builder.add(POWER);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(PULSE, 0)
                .setValue(ACTIVATED, false)
                .setValue(POWER, 0);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (!world.isClientSide && handIn == Hand.MAIN_HAND){
            world.setBlockAndUpdate(pos, state.cycle(PULSE));
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        world.setBlockAndUpdate(pos, state.setValue(POWER, 0).setValue(ACTIVATED, false));
        super.tick(state, world, pos, rand);
    }

    @Override
    public int getSignal(BlockState state, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
        return state.getValue(POWER);
    }

    public static void processClick(BlockPos sender, BlockPos target, World world){

        BlockState state = world.getBlockState(target);
        if(state.getValue(ACTIVATED)) return;

        Vector3d send = new Vector3d(sender.getX(), sender.getY(), sender.getZ());
        Vector3d targ = new Vector3d(target.getX(), target.getY(), target.getZ());

        double dist = send.distanceTo(targ);

        if(dist <= 15){
            int pow = 16-(int)Math.ceil(dist);
            world.setBlockAndUpdate(target, state.setValue(POWER, pow).setValue(ACTIVATED, true));
            world.getBlockTicks().scheduleTick(target, state.getBlock(), 2*(state.getValue(PULSE)+1));
            world.getServer().getLevel(world.dimension()).sendParticles(ParticleTypes.NOTE, target.getX()+.5,target.getY()+.35,target.getZ()+.5,1,0,0,0,0);
            world.playSound(null, target, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 1);
        }
    }

    public static void processAll(BlockPos sender, World world){
        List<BlockPos> positions = new ArrayList<>();

        for(int x = -15; x <= 15; ++x){
            for(int y = -15; y <= 15; ++y){
                for(int z = -15; z <= 15; ++z){
                    BlockPos check = new BlockPos(sender.getX()+x, sender.getY()+y, sender.getZ()+z);
                    if(world.getBlockState(check).getBlock() == ModBlocks.CLICK_DETECTOR.get()) positions.add(check);
                }
            }
        }

        positions.forEach(blockPos -> {
            processClick(sender, blockPos, world);
        });
    }
}
