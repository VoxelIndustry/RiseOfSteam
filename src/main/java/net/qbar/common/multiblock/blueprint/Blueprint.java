package net.qbar.common.multiblock.blueprint;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.multiblock.MultiblockComponent;

import java.util.ArrayList;

@Getter
public class Blueprint implements IMachineComponent
{
    @Getter
    @Setter
    private       MachineDescriptor descriptor;
    private int               rodAmount;
    private ItemStack         rodStack;

    private final ArrayList<ArrayList<ItemStack>> steps;
    private final ArrayList<Integer>              stepsTime;

    private ArrayList<MultiblockStep> multiblockSteps;

    public Blueprint()
    {
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

    @Override
    public void setDescriptor(MachineDescriptor descriptor)
    {
        this.descriptor = descriptor;

        this.rodAmount = descriptor.get(MultiblockComponent.class).getBlockCount();
        this.rodStack = new ItemStack(QBarItems.IRON_ROD, this.getRodAmount());
    }

    @Override
    public MachineDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
