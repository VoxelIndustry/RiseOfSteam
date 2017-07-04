package net.qbar.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

public class QBarOreGenerator implements IWorldGenerator
{
    private HashMap<ChunkPos, GenLeftOver> leftOvers;

    private QBarOreGenerator()
    {
        this.leftOvers = new HashMap<>();
    }

    private static QBarOreGenerator INSTANCE;

    public static final QBarOreGenerator instance()
    {
        if (INSTANCE == null)
            INSTANCE = new QBarOreGenerator();
        return INSTANCE;
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
        int y;
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                BlockPos current = new BlockPos(chunkX * 16 + x, 64, chunkZ * 16 + z);
                if (w.getBiome(current) == Biomes.RIVER)
                {
                    if (rand.nextFloat() < 0.005f)
                    {
                        int count = 0;
                        y = w.rand.nextInt(37) + 17;
                        for (int i = 0; i < 5; i++)
                        {
                            int xShift = rand.nextBoolean() ? Math.max(rand.nextInt(15), 10)
                                    : Math.max(-rand.nextInt(15), -10);
                            int yShift = rand.nextBoolean() ? Math.max(rand.nextInt(12), 3)
                                    : Math.max(-rand.nextInt(9), -3);
                            int zShift = rand.nextBoolean() ? Math.max(rand.nextInt(15), 10)
                                    : Math.max(-rand.nextInt(15), -10);

                            BlockPos veinLocation = new BlockPos(current.getX() + xShift, y + yShift,
                                    current.getZ() + zShift);
                            count += this.generatePlate(w, veinLocation, Blocks.IRON_BLOCK.getDefaultState(), 10, 5,
                                    0.7f);
                        }
                    }
                }
            }
        }
    }

    private int generateSphere(World w, BlockPos center, IBlockState state, int radius, float density)
    {
        int count = 0;
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
                    {
                        this.placeBlock(w, current, state);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int generatePlate(World w, BlockPos center, IBlockState state, int radius, int thickness, float density)
    {
        int count = 0;

        if (thickness % 2 == 0)
            thickness--;

        for (int y = -thickness / 2; y <= thickness / 2; y++)
        {
            int currentRadius = radius - (Math.abs(y));

            int x = currentRadius;
            int z = 0;
            int err = 0;
            while (x >= z)
            {
                count += this.drawXLine(w, center.add(-z, y, x), center.add(z, y, x), state, density);
                count += this.drawXLine(w, center.add(-x, y, z), center.add(x, y, z), state, density);

                count += this.drawXLine(w, center.add(-x, y, -z), center.add(x, y, -z), state, density);
                count += this.drawXLine(w, center.add(-z, y, -x), center.add(z, y, -x), state, density);
                z++;

                if (err <= 0)
                    err += 2 * z + 1;
                else
                {
                    x--;
                    err += 2 * (z - x) + 1;
                }
            }
        }
        return count;
    }

    private int drawXLine(World w, BlockPos first, BlockPos second, IBlockState state, float density)
    {
        int count = 0;
        for (int i = 0; i < (second.getX() - first.getX()); i++)
        {
            if (w.rand.nextFloat() <= density)
            {
                this.placeBlock(w, first.add(i, 0, 0), state);
                count++;
            }
        }
        return count;
    }

    private void placeBlock(World w, BlockPos pos, IBlockState state)
    {
        if (w.isChunkGeneratedAt(new ChunkPos(pos).x, new ChunkPos(pos).z))
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

    @SubscribeEvent
    public void onOreGen(OreGenEvent.GenerateMinable e)
    {
        if (e.getType().equals(OreGenEvent.GenerateMinable.EventType.IRON)
                || e.getType().equals(OreGenEvent.GenerateMinable.EventType.GOLD)
                || e.getType().equals(OreGenEvent.GenerateMinable.EventType.REDSTONE))
            e.setResult(Event.Result.DENY);
    }
}
