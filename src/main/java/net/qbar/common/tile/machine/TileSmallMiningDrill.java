package net.qbar.common.tile.machine;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;

public class TileSmallMiningDrill extends TileInventoryBase implements ITileMultiblockCore
{
    public TileSmallMiningDrill()
    {
        super("smallminingdrill", 1);
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }
}
