package net.qbar.common.multiblock;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public interface ITileMultiblockCore extends ITileMultiblock
{
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing);

    @Nullable
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing);
}
