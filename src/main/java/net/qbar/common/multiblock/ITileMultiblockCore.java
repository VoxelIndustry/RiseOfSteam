package net.qbar.common.multiblock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.grid.CableGrid;

import javax.annotation.Nullable;

public interface ITileMultiblockCore extends ITileMultiblock
{
    @Override
    default boolean isCore()
    {
        return true;
    }

    @Override
    default boolean isCorePresent()
    {
        return true;
    }

    @Override
    default ITileMultiblockCore getCore()
    {
        return this;
    }

    boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing);

    @Nullable
    <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing);

    default boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                 final float hitZ, BlockPos from)
    {
        return false;
    }

    default BlockPos getCoreOffset()
    {
        return BlockPos.ORIGIN;
    }

    default void connectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {

    }

    default void disconnectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {

    }
}
