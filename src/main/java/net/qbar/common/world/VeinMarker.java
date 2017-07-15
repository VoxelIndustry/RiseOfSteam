package net.qbar.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface VeinMarker extends BiConsumer<World, BlockPos>
{
    void accept(World w, BlockPos pos);
}
