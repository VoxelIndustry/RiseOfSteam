package net.qbar.common.machine.module.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.qbar.common.machine.component.CraftingComponent;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.InventoryModule;
import net.qbar.common.recipe.QBarRecipeHandler;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;

/**
 * Special InventoryModule for uses with crafters.
 * UP is for inputs
 * DOWN is for outputs
 * Everything else is union of I/O
 */
public class CraftingInventoryModule extends InventoryModule
{
    private CraftingComponent crafter;

    public CraftingInventoryModule(IModularMachine machine)
    {
        super(machine, "CraftingInventoryModule",
                machine.getDescriptor().get(CraftingComponent.class).getInventorySize());

        this.crafter = machine.getDescriptor().get(CraftingComponent.class);
    }

    @Override
    @Nonnull
    public int[] getSlotsForFace(@Nonnull EnumFacing side)
    {
        switch (side)
        {
            case DOWN:
                return this.crafter.getOutputs();
            case UP:
                return this.crafter.getInputs();
            default:
                return this.crafter.getIoUnion();
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return index < this.crafter.getInputs().length
                && QBarRecipeHandler.inputMatchWithoutCount(this.crafter.getRecipeCategory(), index, stack);
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing side)
    {
        return ArrayUtils.contains(this.crafter.getInputs(), index)
                && this.isBufferEmpty()
                && this.isOutputEmpty()
                && this.isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing side)
    {
        return ArrayUtils.contains(this.crafter.getOutputs(), index);
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public boolean isBufferEmpty()
    {
        for (int i = 0; i < this.crafter.getBuffers().length; i++)
        {
            if (!this.getStackInSlot(this.crafter.getBuffers()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isInputEmpty()
    {
        for (int i = 0; i < this.crafter.getInputs().length; i++)
        {
            if (!this.getStackInSlot(this.crafter.getInputs()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isOutputEmpty()
    {
        for (int i = 0; i < this.crafter.getOutputs().length; i++)
        {
            if (!this.getStackInSlot(this.crafter.getOutputs()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public int[] getInputSlots()
    {
        return this.crafter.getInputs();
    }

    public int[] getOutputSlots()
    {
        return this.crafter.getOutputs();
    }

    public int[] getBufferSlots()
    {
        return this.crafter.getBuffers();
    }
}
