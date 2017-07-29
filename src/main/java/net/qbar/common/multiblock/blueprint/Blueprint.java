package net.qbar.common.multiblock.blueprint;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.IMultiblockDescriptor;

import java.util.ArrayList;

@Getter
public class Blueprint
{
    private final String                name;
    private final IMultiblockDescriptor multiblock;
    private final int                   rodAmount;
    private final ItemStack             rodStack;

    private final ArrayList<ArrayList<ItemStack>> steps;
    private final ArrayList<Integer>              stepsTime;

    private ArrayList<MultiblockStep> multiblockSteps;

    public Blueprint(final String name, final IMultiblockDescriptor multiblock, final int rodAmount)
    {
        this.name = name;
        this.multiblock = multiblock;
        this.rodAmount = rodAmount;

        this.rodStack = new ItemStack(QBarItems.IRON_ROD, this.getRodAmount());

        this.steps = new ArrayList<>();
        this.stepsTime = new ArrayList<>();
        this.multiblockSteps = new ArrayList<>();
    }

    public Blueprint addStep(final int time, final ItemStack... stacks)
    {
        this.stepsTime.add(time);
        this.steps.add(Lists.newArrayList(stacks));
        return this;
    }

    public void setMultiblockSteps(final ArrayList<MultiblockStep> multiblockSteps)
    {
        this.multiblockSteps = multiblockSteps;
        this.multiblockSteps.forEach(step -> step.reloadStates());
    }
}
