package net.qbar.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface VeinMarker
{
    void accept(World w, BlockPos pos, int actualHeapSize, int heapSize);
}
