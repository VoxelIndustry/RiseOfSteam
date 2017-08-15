package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;

public class TileAlloyCauldron extends TileInventoryBase
        implements ITileMultiblockCore, ITickable, ISidedInventory, IContainerProvider
{
    public TileAlloyCauldron()
    {
        super("alloycauldron", 1);
    }

    @Override
    public void update()
    {

    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return false;
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
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return null;
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
