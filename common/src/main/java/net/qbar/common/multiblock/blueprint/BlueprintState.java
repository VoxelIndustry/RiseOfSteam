package net.qbar.common.multiblock.blueprint;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.qbar.common.util.ItemUtils;

import java.util.List;

public class BlueprintState
{
    @Getter
    private final Blueprint        blueprint;
    private NonNullList<ItemStack> currentStacks;
    @Getter
    private int                    currentStep;
    @Getter
    @Setter
    private int                    currentTime;

    private boolean                isStepStackComplete;

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
        this.isStepStackComplete = tag.getBoolean("isStepStackComplete");

        this.currentStacks = NonNullList.withSize(tag.getTagList("Items", 10).tagCount(), ItemStack.EMPTY);
        ItemUtils.loadAllItems(tag, this.currentStacks);
    }

    public NBTTagCompound toNBT()
    {
        final NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("currentStep", this.currentStep);
        tag.setInteger("currentTime", this.currentTime);
        tag.setBoolean("isStepStackComplete", this.isStepStackComplete);
        ItemStackHelper.saveAllItems(tag, this.currentStacks);
        return tag;
    }

    public void setCurrentStep(final int currentStep)
    {
        this.currentStep = currentStep;

        this.currentStacks.clear();
        if (this.blueprint.getSteps().size() > currentStep)
        {
            this.currentStacks = NonNullList.withSize(this.blueprint.getSteps().get(currentStep).size(), ItemStack.EMPTY);
            int i = 0;
            for (ItemStack stack : this.blueprint.getSteps().get(currentStep))
            {
                final ItemStack copy = stack.copy();
                copy.setCount(1);
                this.currentStacks.set(i, copy);
                i++;
            }
        }
        this.isStepStackComplete = false;
        this.currentTime = 0;
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

    public boolean needStack()
    {
        if (this.isStepStackComplete)
            return false;
        int index = 0;
        for (final ItemStack stack : this.currentStacks)
        {
            if (stack.getCount() - 1 != this.blueprint.getSteps().get(this.currentStep).get(index).getCount())
                return true;
            index++;
        }
        return false;
    }

    public void setStepStackComplete()
    {
        this.isStepStackComplete = true;
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

    public List<ItemStack> getStepStacks()
    {
        return this.blueprint.getSteps().get(this.currentStep);
    }

    public int getStepTime()
    {
        return this.blueprint.getStepsTime().get(this.currentStep);
    }
}
