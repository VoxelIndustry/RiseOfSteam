package net.qbar.common.machine.module;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.qbar.common.util.ItemUtils;

import javax.annotation.Nonnull;

@Getter
public abstract class InventoryModule extends MachineModule implements ISerializableModule, ISidedInventory
{
    private String                 inventoryName;
    private NonNullList<ItemStack> stacks;

    public InventoryModule(IModularMachine machine, String name, int slotCount)
    {
        super(machine, name);

        this.stacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        ItemUtils.loadAllItems(tag, this.stacks);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        ItemUtils.saveAllItems(tag, this.stacks);
        return tag;
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
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
                : new TextComponentTranslation("gui." + this.getName() + ".name", new Object[0]);
    }

    @Override
    public void markDirty()
    {
        this.getMachineTile().markDirty();
    }
}
