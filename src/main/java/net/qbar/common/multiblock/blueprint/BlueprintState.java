package net.qbar.common.multiblock.blueprint;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.qbar.common.util.ItemUtils;

public class BlueprintState
{
    private final Blueprint              blueprint;
    private final NonNullList<ItemStack> currentStacks;
    private int                          currentStep;
    private int                          currentTime;

    public BlueprintState(final Blueprint blueprint)
    {
        this.blueprint = blueprint;

        this.currentStacks = NonNullList.create();
        this.setCurrentStep(0);
    }

    public BlueprintState(final Blueprint blueprint, final NBTTagCompound tag)
    {
        this(blueprint);

        this.currentStep = tag.getInteger("currentStep");
        this.currentTime = tag.getInteger("currentTime");
        ItemStackHelper.loadAllItems(tag, this.currentStacks);
    }

    public NBTTagCompound toNBT()
    {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("currentStep", this.currentStep);
        tag.setInteger("currentTime", this.currentTime);
        ItemStackHelper.saveAllItems(tag, this.currentStacks);
        return tag;
    }

    public Blueprint getBlueprint()
    {
        return this.blueprint;
    }

    public int getCurrentStep()
    {
        return this.currentStep;
    }

    public void setCurrentStep(final int currentStep)
    {
        this.currentStep = currentStep;

        this.currentStacks.clear();
        if (this.blueprint.getSteps().size() > currentStep)
            this.blueprint.getSteps().get(currentStep).forEach(stack ->
            {
                final ItemStack copy = stack.copy();
                copy.setCount(1);
                this.currentStacks.add(copy);
            });
        this.currentTime = 0;
    }

    public int getCurrentTime()
    {
        return this.currentTime;
    }

    public void setCurrentTime(final int currentTime)
    {
        this.currentTime = currentTime;
    }

    public int addStack(final ItemStack stack)
    {
        final ItemStack toDecr = this.currentStacks.stream().filter(stack2 -> ItemUtils.deepEquals(stack2, stack))
                .findFirst().get();

        if (toDecr.getCount() - 1 > stack.getCount())
        {
            toDecr.setCount(stack.getCount() + 1);
            return stack.getCount() - toDecr.getCount() - 1;
        }
        final int grown = stack.getCount();
        toDecr.grow(stack.getCount());
        return grown;
    }

    public boolean acceptStack(final ItemStack stack)
    {
        return this.currentStacks.stream().anyMatch(stack2 -> ItemUtils.deepEquals(stack2, stack));
    }

    public boolean isCurrentComplete()
    {
        int index = 0;
        for (final ItemStack stack : this.currentStacks)
        {
            if (stack.getCount() - 1 != this.blueprint.getSteps().get(this.currentStep).get(index).getCount())
                return false;
            index++;
        }
        return true;
    }

    public NonNullList<ItemStack> getCurrentStacks()
    {
        return this.currentStacks;
    }

    public MultiblockStep getMultiblockStep()
    {
        if (this.blueprint.getMultiblockSteps().size() > this.currentStep)
            return this.blueprint.getMultiblockSteps().get(this.currentStep);
        return null;
    }
}
