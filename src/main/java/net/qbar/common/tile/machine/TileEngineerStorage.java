package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class TileEngineerStorage extends TileInventoryBase implements IContainerProvider, ISidedInventory
{
    protected final IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    public TileEngineerStorage()
    {
        super("engineerstorage", 32);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return IntStream.range(0, 32).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return index >= 0 && index <= 31;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 31;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.inventoryHandler;
        return super.getCapability(capability, facing);
    }
}
