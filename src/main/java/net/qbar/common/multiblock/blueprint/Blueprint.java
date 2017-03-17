package net.qbar.common.multiblock.blueprint;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.IMultiblockDescriptor;

public class Blueprint
{
    private final String                          name;
    private final IMultiblockDescriptor           multiblock;
    private final int                             rodAmount;
    private final ItemStack                       rodStack;

    private final ArrayList<ArrayList<ItemStack>> steps;
    private final ArrayList<Integer>              stepsTime;

    public Blueprint(final String name, final IMultiblockDescriptor multiblock, final int rodAmount)
    {
        this.name = name;
        this.multiblock = multiblock;
        this.rodAmount = rodAmount;

        this.rodStack = new ItemStack(QBarItems.IRON_ROD, this.getRodAmount());

        this.steps = new ArrayList<>();
        this.stepsTime = new ArrayList<>();
    }

    public Blueprint addStep(final int time, final ItemStack... stacks)
    {
        this.stepsTime.add(time);
        this.steps.add(Lists.newArrayList(stacks));
        return this;
    }

    public String getName()
    {
        return this.name;
    }

    public IMultiblockDescriptor getMultiblock()
    {
        return this.multiblock;
    }

    public int getRodAmount()
    {
        return this.rodAmount;
    }

    public ItemStack getRodStack()
    {
        return this.rodStack;
    }

    public ArrayList<ArrayList<ItemStack>> getSteps()
    {
        return this.steps;
    }

    public ArrayList<Integer> getStepsTime()
    {
        return this.stepsTime;
    }
}
