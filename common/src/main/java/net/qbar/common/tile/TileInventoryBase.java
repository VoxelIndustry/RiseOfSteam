package net.qbar.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.qbar.common.util.ItemUtils;

import javax.annotation.Nonnull;

public abstract class TileInventoryBase extends QBarTileBase implements IInventory
{
    private final NonNullList<ItemStack> stacks;
    private final String                 name;

    public TileInventoryBase(final String name, final int size)
    {
        this.name = name;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        ItemUtils.saveAllItems(tag, this.stacks);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        ItemUtils.loadAllItems(tag, this.stacks);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public boolean hasCustomName()
    {
        return true;
    }

    @Override
    public int getSizeInventory()
    {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty()
    {
        for (final ItemStack itemstack : this.stacks)
            if (!itemstack.isEmpty())
                return false;
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(final int index)
    {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(final int index, final int count)
    {
        return ItemStackHelper.getAndSplit(this.stacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(final int index)
    {
        return ItemStackHelper.getAndRemove(this.stacks, index);
    }

    @Override
    public void setInventorySlotContents(final int index, final ItemStack stack)
    {
        this.stacks.set(index, stack);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(final EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(final EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(final EntityPlayer player)
    {
    }

    @Override
    public boolean isItemValidForSlot(final int index, final ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(final int id)
    {
        return 0;
    }

    @Override
    public void setField(final int id, final int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {

    }

    @Override
    public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName())
                : new TextComponentTranslation(this.getName(), new Object[0]);
    }
}
