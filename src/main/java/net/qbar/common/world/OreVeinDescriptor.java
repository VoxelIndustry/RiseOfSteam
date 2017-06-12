package net.qbar.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.List;

public class OreVeinDescriptor
{
    private HashMap<IBlockState, Float> contents;
    private List<Biome>                 biomes;

    private float                       heapDensity;
    private int                         heapQty;
    private int                         heapSize;
    private EVeinForm                   veinForm;
}
