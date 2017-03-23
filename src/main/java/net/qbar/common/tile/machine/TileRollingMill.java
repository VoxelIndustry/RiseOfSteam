package net.qbar.common.tile.machine;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.TileInventoryBase;

public class TileRollingMill extends TileInventoryBase implements ITileMultiblockCore, ITickable, ISidedInventory
{
    public TileRollingMill()
    {
        super("rollingmill", 2);
    }

    @Override
    public void update()
    {

    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return null;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }
}
