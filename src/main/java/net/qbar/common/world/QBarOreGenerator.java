package net.qbar.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.qbar.common.util.PosUtils;

import java.util.Random;
import java.util.stream.Stream;

public class QBarOreGenerator implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
            IChunkProvider chunkProvider)
    {
        if (world.provider.getDimension() == 0)
        {
            generateCopper(random, chunkX, chunkZ, world);
        }
    }

    private void generateDebug(Random rand, int chunkX, int chunkZ, World w, int y, IBlockState block, Biome... biome)
    {
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                BlockPos current = new BlockPos(chunkX * 16 + i + 8, y, chunkZ * 16 + j + 8);
                if (Stream.of(biome).anyMatch(match -> match == w.getBiome(current)))
                {
                    w.setBlockState(current, block, 2);
                }
            }
        }
    }

    private void generateCopper(Random rand, int chunkX, int chunkZ, World w)
    {
        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                BlockPos current = new BlockPos(chunkX * 16 + i, 64, chunkZ * 16 + j);
                if (w.getBiome(current) == Biomes.RIVER)
                {
                    w.setBlockState(current, Blocks.DIAMOND_BLOCK.getDefaultState(), 2);

                    if (rand.nextFloat() < 0.001f)
                    {
                        this.generateSphere(w, current, Blocks.IRON_ORE.getDefaultState(), 10, 0.7f);
                    }
                }
            }
        }
    }

    private void generateSphere(World w, BlockPos center, IBlockState state, int radius, float density)
    {
        int radiusSquared = radius * radius * radius;

        for (int x = -radius; x < radius; x++)
        {
            for (int y = -radius; y < radius; y++)
            {
                for (int z = -radius; z < radius; z++)
                {
                    BlockPos current = center.add(x, y, z);

                    w.setBlockState(current, Blocks.GLASS.getDefaultState());

                    double distance = Math.abs(center.getDistance(current.getX(), current.getY(), current.getZ()));
                    if ((distance * distance * distance) < radiusSquared && w.rand.nextFloat() <= density)
                        w.setBlockState(current, state);
                }
            }
        }
    }

    private void generatePlate(World w, BlockPos center, IBlockState state, int radius, int thickness, float density)
    {
        for (int y = -thickness / 2; y < thickness / 2; y++)
        {
            int currentRadius = radius - (Math.abs(y));
            int radiusSquared = currentRadius * currentRadius;
            for (int x = -currentRadius / 2; x < currentRadius / 2; x++)
            {
                for (int z = -currentRadius / 2; z < currentRadius / 2; z++)
                {
                    BlockPos current = center.add(x, center.getY() + thickness, z);

                    if (PosUtils.posDotProd(center, current) < radiusSquared && w.rand.nextFloat() <= density)
                        w.setBlockState(current, state);
                }
            }
        }
    }
}
