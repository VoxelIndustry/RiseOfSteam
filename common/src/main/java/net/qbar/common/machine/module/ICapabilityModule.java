package net.qbar.common.machine.module;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public interface ICapabilityModule
{
    boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing);

    @Nullable
    <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing);
}
