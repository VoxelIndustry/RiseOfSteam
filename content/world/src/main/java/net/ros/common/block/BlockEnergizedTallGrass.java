package net.ros.common.block;

import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;

import java.util.Random;

public class BlockEnergizedTallGrass extends BlockTallGrass
{
    public BlockEnergizedTallGrass(String name)
    {
        this.setSoundType(SoundType.PLANT);

        this.setRegistryName(ROSConstants.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(ROSConstants.TAB_ALL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        if (world.getWorldTime() % 4 == 0)
            this.spawnParticles(world, pos);
    }

    private void spawnParticles(World world, BlockPos pos)
    {
        Random random = world.rand;

        for (int i = 0; i < 6; ++i)
        {
            double d1 = (double) ((float) pos.getX() + random.nextFloat());
            double d2 = (double) ((float) pos.getY() + random.nextFloat());
            double d3 = (double) ((float) pos.getZ() + random.nextFloat());

            if (i == 0 && !world.getBlockState(pos.up()).isOpaqueCube())
            {
                d2 = (double) pos.getY() + 0.0625D + 1.0D;
            }

            if (i == 1 && !world.getBlockState(pos.down()).isOpaqueCube())
            {
                d2 = (double) pos.getY() - 0.0625D;
            }

            if (i == 2 && !world.getBlockState(pos.south()).isOpaqueCube())
            {
                d3 = (double) pos.getZ() + 0.0625D + 1.0D;
            }

            if (i == 3 && !world.getBlockState(pos.north()).isOpaqueCube())
            {
                d3 = (double) pos.getZ() - 0.0625D;
            }

            if (i == 4 && !world.getBlockState(pos.east()).isOpaqueCube())
            {
                d1 = (double) pos.getX() + 0.0625D + 1.0D;
            }

            if (i == 5 && !world.getBlockState(pos.west()).isOpaqueCube())
            {
                d1 = (double) pos.getX() - 0.0625D;
            }

            if (d1 < (double) pos.getX() || d1 > (double) (pos.getX() + 1) || d2 < 0.0D
                    || d2 > (double) (pos.getY() + 1) || d3 < (double) pos.getZ() || d3 > (double) (pos.getZ() + 1))
            {
                world.spawnParticle(EnumParticleTypes.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return 7;
    }
}
