package net.qbar.common.multiblock;

import net.minecraft.util.math.BlockPos;

public interface ITileMultiblock
{
    boolean isCore();

    boolean isCorePresent();

    void breakCore();

    BlockPos getCorePos();

    ITileMultiblockCore getCore();

    BlockPos getCoreOffset();
}
