package net.ros.common.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.impl.CableGrid;

import javax.annotation.Nullable;

public interface ITileMultiblockCore extends ITileMultiblock
{
    void breakCore();

    boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing);

    @Nullable
    <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing);

    default ITileMultiblockCore getCore()
    {
        return this;
    }

    default BlockPos getCoreOffset()
    {
        return BlockPos.ORIGIN;
    }

    default boolean isCore()
    {
        return true;
    }

    default boolean isCorePresent()
    {
        return true;
    }

    default boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ,
                                 BlockPos from)
    {
        return false;
    }

    default void connectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {

    }

    default void disconnectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {

    }
}
