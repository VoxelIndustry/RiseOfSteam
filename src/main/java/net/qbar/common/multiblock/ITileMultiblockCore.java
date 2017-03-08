package net.qbar.common.multiblock;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

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

    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing);

    @Nullable
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing);

    public default boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX,
            final float hitY, final float hitZ)
    {
        return false;
    }
}
