package net.qbar.common.world;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.qbar.QBar;
import org.apache.commons.lang3.tuple.Pair;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QBarOreGenerator implements IWorldGenerator
{
    private      ConcurrentHashMap<ChunkPos, GenLeftOver> leftOvers;
    private      List<ChunkPos>                           generateds;
    public final Predicate<IBlockState>                   STONE_PREDICATE;

    private QBarOreGenerator()
    {
        this.leftOvers = new ConcurrentHashMap<>();
        this.generateds = new ArrayList<>();

        this.STONE_PREDICATE = state -> state != null && state.getBlock() == Blocks.STONE
                && state.getValue(BlockStone.VARIANT).isNatural();
        QBarVeins.initVeins();
    }

    private static QBarOreGenerator INSTANCE;

    public static QBarOreGenerator instance()
    {
        if (INSTANCE == null)
            INSTANCE = new QBarOreGenerator();
        return INSTANCE;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
                         IChunkProvider chunkProvider)
    {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

        generateds.add(chunkPos);
        if (world.provider.getDimension() == 0)
            generateVeins(random, chunkX, chunkZ, world);
    }

    private void generateVeins(Random rand, int chunkX, int chunkZ, World world)
    {
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                BlockPos center = new BlockPos(x + (chunkX * 16), 64, z + (chunkZ * 16));
                QBarVeins.VEINS.forEach(vein -> generateVein(rand, center, world, vein));
            }
        }
    }

    private void generateVein(Random rand, BlockPos center, World world, OreVeinDescriptor vein)
    {
        int y;
        if (vein.getBiomeMatcher().test(world.getBiome(center)) && rand.nextFloat() < vein.getRarity())
        {
            y = rand.nextInt(vein.getHeightRange().getMaximum() - vein.getHeightRange().getMinimum())
                    + vein.getHeightRange().getMinimum();
            for (int i = 0; i < vein.getHeapQty(); i++)
            {
                int xShift = 0;
                int yShift = 0;
                int zShift = 0;

                switch (vein.getVeinForm())
                {
                    case FLAT:
                        xShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt((int) (vein.getHeapSize() * 1.5f)), vein.getHeapSize())
                                : Math.max(-rand.nextInt((int) (vein.getHeapSize() * 1.5f)), -vein.getHeapSize());
                        yShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 4)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 4);
                        zShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt((int) (vein.getHeapSize() * 1.5f)), vein.getHeapSize())
                                : Math.max(-rand.nextInt((int) (vein.getHeapSize() * 1.5f)), -vein.getHeapSize());
                        break;
                    case SCATTERED:
                        xShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt(vein.getHeapSize() * 4), vein.getHeapSize() * 2)
                                : Math.max(-rand.nextInt(vein.getHeapSize() * 4), -vein.getHeapSize() * 2);
                        yShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt(vein.getHeapSize() * 4), vein.getHeapSize() * 2)
                                : Math.max(-rand.nextInt(vein.getHeapSize() * 4), -vein.getHeapSize() * 2);
                        zShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt(vein.getHeapSize() * 4), vein.getHeapSize() * 2)
                                : Math.max(-rand.nextInt(vein.getHeapSize() * 4), -vein.getHeapSize() * 2);
                        break;
                    case UPWARD:
                        xShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 4)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 4);
                        yShift = rand.nextBoolean()
                                ? Math.max(rand.nextInt((int) (vein.getHeapSize() * 1.5f)), vein.getHeapSize())
                                : Math.max(-rand.nextInt((int) (vein.getHeapSize() * 1.5f)), -vein.getHeapSize());
                        zShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 4)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 4);
                        break;
                    case MERGED:
                        xShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 3)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 3);
                        yShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 3)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 3);
                        zShift = rand.nextBoolean() ? Math.max(rand.nextInt(vein.getHeapSize()), vein.getHeapSize() / 3)
                                : Math.max(-rand.nextInt(vein.getHeapSize()), -vein.getHeapSize() / 3);
                        break;
                }

                BlockPos veinLocation = new BlockPos(center.getX() + xShift, y + yShift, center.getZ() + zShift);
                switch (vein.getHeapForm())
                {
                    case SPHERES:
                        this.generateSphere(world, veinLocation, vein.getContents(), vein.getHeapSize(),
                                vein.getHeapDensity());
                        break;
                    case PLATES:
                        this.generatePlate(world, veinLocation, vein.getContents(), vein.getHeapSize(),
                                vein.getHeapSize() / 2, vein.getHeapDensity());
                        break;
                    case SHATTERED:
                        this.generateSphere(world, veinLocation, vein.getContents(), vein.getHeapSize(),
                                vein.getHeapDensity());
                        break;
                }
            }
        }
    }

    private int generateSphere(World w, BlockPos center, List<Pair<IBlockState, Float>> contents, int radius,
                               float density)
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
                        this.placeBlock(w, current, this.randomState(w.rand, contents));
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int generatePlate(World w, BlockPos center, List<Pair<IBlockState, Float>> contents, int radius, int thickness,
                              float density)
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
                count += this.drawXLine(w, center.add(-z, y, x), center.add(z, y, x), contents, density);
                count += this.drawXLine(w, center.add(-x, y, z), center.add(x, y, z), contents, density);

                count += this.drawXLine(w, center.add(-x, y, -z), center.add(x, y, -z), contents, density);
                count += this.drawXLine(w, center.add(-z, y, -x), center.add(z, y, -x), contents, density);
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

    private int drawXLine(World w, BlockPos first, BlockPos second, List<Pair<IBlockState, Float>> contents, float density)
    {
        int count = 0;
        for (int i = 0; i < (second.getX() - first.getX()); i++)
        {
            if (w.rand.nextFloat() <= density)
            {
                this.placeBlock(w, first.add(i, 0, 0), this.randomState(w.rand, contents));
                count++;
            }
        }
        return count;
    }

    private void placeBlock(World w, BlockPos pos, IBlockState state)
    {
        ChunkPos chunk = new ChunkPos(pos);

        if (chunk.getXStart() == pos.getX())
            chunk = new ChunkPos(chunk.x - 1, chunk.z);
        else if (chunk.getXEnd() == pos.getX())
            chunk = new ChunkPos(chunk.x + 1, chunk.z);
        else if (chunk.getZStart() == pos.getZ())
            chunk = new ChunkPos(chunk.x, chunk.z - 1);
        else if (chunk.getZEnd() == pos.getZ())
            chunk = new ChunkPos(chunk.x, chunk.z + 1);

        if (!this.leftOvers.containsKey(chunk))
        {
            GenLeftOver leftOver = new GenLeftOver(chunk);
            this.leftOvers.put(chunk, leftOver);
        }
        this.leftOvers.get(chunk).getBlocks().put(pos, state);
    }

    private IBlockState randomState(Random rand, List<Pair<IBlockState, Float>> choices)
    {
        float p = choices.get(0).getRight();
        float x = rand.nextFloat();

        int i = 0;
        while (x > p)
        {
            i++;
            p += choices.get(i).getRight();
        }
        return choices.get(i).getLeft();
    }

    @SubscribeEvent
    public void onOreGen(OreGenEvent.GenerateMinable e)
    {
        if (e.getType().equals(OreGenEvent.GenerateMinable.EventType.IRON)
                || e.getType().equals(OreGenEvent.GenerateMinable.EventType.GOLD)
                || e.getType().equals(OreGenEvent.GenerateMinable.EventType.REDSTONE))
            e.setResult(Event.Result.DENY);
    }

    private static final int BLOCKS_PER_TICK = 10_000;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e)
    {
        if (!(e.world instanceof WorldServer))
            return;
        if (e.phase.equals(TickEvent.Phase.END) && !this.leftOvers.isEmpty())
        {
            int generated = 0;

            Iterator<Map.Entry<ChunkPos, GenLeftOver>> iterator = this.leftOvers.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<ChunkPos, GenLeftOver> leftOver = iterator.next();
                if (e.world.isChunkGeneratedAt(leftOver.getKey().x, leftOver.getKey().z))
                    generated += leftOver.getValue().generate(e.world, BLOCKS_PER_TICK - generated);
                if (leftOver.getValue().getBlocks().isEmpty())
                    iterator.remove();
                if (generated >= BLOCKS_PER_TICK)
                    break;
            }
            if (generated > 0)
                QBar.logger.info("{} blocks generated. {} left or unreachable.", NumberFormat.getIntegerInstance().format(generated),
                        NumberFormat.getIntegerInstance().format(this.leftOvers.values().stream().mapToInt(leftOver ->
                                leftOver.getBlocks().size()).sum()));
        }
    }
}
