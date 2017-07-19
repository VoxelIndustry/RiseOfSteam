package net.qbar.common.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class FeatureGenerator
{
    static int generateSphere(World w, BlockPos center, VeinBlockSupplier blockSupplier, int radius,
                              float density, Predicate<IBlockState> terrainPredicate)
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
                        placeBlock(w, current, blockSupplier.supply(w.rand, radius, (float) distance), terrainPredicate);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    static int generatePlate(World w, BlockPos center, VeinBlockSupplier blockSupplier, int radius,
                             int thickness, float density, Predicate<IBlockState> terrainPredicate)
    {
        int count = 0;

        if (thickness % 2 == 0)
            thickness--;

        for (int y = -thickness / 2; y <= thickness / 2; y++)
        {
            int x = radius - (Math.abs(y));
            int z = 0;
            int err = 0;
            while (x >= z)
            {
                count += drawXLine(w, center.add(-z, y, x), center.add(z, y, x), blockSupplier, center, radius, density, terrainPredicate);
                count += drawXLine(w, center.add(-x, y, z), center.add(x, y, z), blockSupplier, center, radius, density, terrainPredicate);

                count += drawXLine(w, center.add(-x, y, -z), center.add(x, y, -z), blockSupplier, center, radius, density, terrainPredicate);
                count += drawXLine(w, center.add(-z, y, -x), center.add(z, y, -x), blockSupplier, center, radius, density, terrainPredicate);
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

    static int generateTallGrassPatch(World w, BlockPos center, List<Pair<IBlockState, Float>> contents, int radius, float density)
    {
        int count = 0;

        for (int x = -radius; x < radius; x++)
        {
            for (int z = -radius; z < radius; z++)
            {
                BlockPos current = center.add(x, 0, z);
                if (w.isBlockLoaded(current))
                {
                    current = w.getTopSolidOrLiquidBlock(current);

                    if (w.getBlockState(current.down()).getMaterial() == Material.GRASS && w.rand.nextFloat() <= density)
                        placeBlock(w, current, randomState(w.rand, contents), state -> true);
                }
            }
        }
        return count;
    }

    static int drawXLine(World w, BlockPos first, BlockPos second, VeinBlockSupplier blockSupplier, BlockPos center, int veinSize, float density,
                         Predicate<IBlockState> terrainPredicate)
    {
        int count = 0;
        for (int i = 0; i < (second.getX() - first.getX()); i++)
        {
            if (w.rand.nextFloat() <= density)
            {
                BlockPos current = first.add(i, 0, 0);
                placeBlock(w, current, blockSupplier.supply(w.rand, veinSize,
                        (float) Math.abs(center.getDistance(current.getX(), current.getY(), current.getZ()))), terrainPredicate);
                count++;
            }
        }
        return count;
    }

    private static void placeBlock(World w, BlockPos pos, IBlockState state, Predicate<IBlockState> terrainPredicate)
    {
        ChunkPos chunk = new ChunkPos(pos);

        if (w.isChunkGeneratedAt(chunk.x, chunk.z))
        {
            IBlockState previousState = w.getBlockState(pos);

            if (previousState.getBlock().isReplaceableOreGen(previousState, w, pos, terrainPredicate::test)
                    && (!isBorder(chunk, pos) || w.isAreaLoaded(pos, 1, false)))
                w.setBlockState(pos, state, 2);
        }
    }

    private static boolean isBorder(ChunkPos chunk, BlockPos pos)
    {
        return chunk.getXStart() == pos.getX() || chunk.getXEnd() == pos.getX() || chunk.getZStart() == pos.getZ()
                || chunk.getZEnd() == pos.getZ();
    }

    static IBlockState randomState(Random rand, List<Pair<IBlockState, Float>> choices)
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
}
