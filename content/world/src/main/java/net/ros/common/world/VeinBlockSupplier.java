package net.ros.common.world;

import net.minecraft.block.state.IBlockState;

import java.util.Random;

@FunctionalInterface
public interface VeinBlockSupplier
{
    IBlockState supply(Random rand, int veinSize, float centerOffset);
}
