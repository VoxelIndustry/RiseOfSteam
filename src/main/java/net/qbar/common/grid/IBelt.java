package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IBelt extends ITileCable<BeltGrid>
{
    boolean isSlope();

    EnumFacing getFacing();

    void extractItems();

    void connectInput(BlockPos pos);
}
