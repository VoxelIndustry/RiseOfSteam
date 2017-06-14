package net.qbar.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.qbar.common.util.PosUtils;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

public class QBarOreGenerator implements IWorldGenerator
{
    private HashMap<ChunkPos, GenLeftOver> leftOvers;

    public QBarOreGenerator()
    {
        this.leftOvers = new HashMap<>();
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
            IChunkProvider chunkProvider)
    {
        if (world.provider.getDimension() == 0)
        {
            if (leftOvers.containsKey(new ChunkPos(chunkX, chunkZ)))
            {
                leftOvers.get(new ChunkPos(chunkX, chunkZ)).generate(world);
                leftOvers.remove(new ChunkPos(chunkX, chunkZ));
            }
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

                    if (rand.nextFloat() < 0.01f)
                    {
                        this.generateSphere(w, new ChunkPos(chunkX, chunkZ), current, Blocks.IRON_ORE.getDefaultState(),
                                10, 0.7f);
                    }
                }
            }
        }
    }

    private void generateSphere(World w, ChunkPos originChunk, BlockPos center, IBlockState state, int radius,
            float density)
    {
        int radiusSquared = radius * radius * radius;

        for (int x = -radius; x < radius; x++)
        {
            for (int y = -radius; y < radius; y++)
            {
                for (int z = -radius; z < radius; z++)
                {
                    BlockPos current = center.add(x, y, z);

                    double distance = Math.abs(center.getDistance(current.getX(), current.getY(), current.getZ()));
                    if ((distance * distance * distance) < radiusSquared && w.rand.nextFloat() <= density)
                        this.placeBlock(w, current, state, originChunk);
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

    private void placeBlock(World w, BlockPos pos, IBlockState state, ChunkPos originChunk)
    {
        if (w.getChunkFromBlockCoords(pos).isLoaded() || w.getChunkFromBlockCoords(pos).getPos().equals(originChunk))
            w.setBlockState(pos, state);
        else
        {
            ChunkPos chunk = new ChunkPos(pos);
            if (!this.leftOvers.containsKey(chunk))
            {
                GenLeftOver leftOver = new GenLeftOver(chunk);
                this.leftOvers.put(chunk, leftOver);
            }
            this.leftOvers.get(chunk).getBlocks().put(pos, state);
        }
    }
}
