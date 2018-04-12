package net.qbar.common.machine.module.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.grid.node.IBelt;
import net.qbar.common.machine.OutputPoint;
import net.qbar.common.machine.component.AutomationComponent;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ITickableModule;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;

import java.util.HashMap;

public class AutomationModule extends MachineModule implements ITickableModule
{
    private final HashMap<OutputPoint, Integer> lastOutput;

    private final CraftingInventoryModule inventory;

    private final SidedInvWrapper outputWrapper;

    public AutomationModule(IModularMachine machine)
    {
        super(machine, "AutomationModule");

        this.lastOutput = new HashMap<>();
        this.inventory = machine.getModule(CraftingInventoryModule.class);
        this.outputWrapper = new SidedInvWrapper(this.inventory, EnumFacing.NORTH);
    }

    @Override
    public void tick()
    {
        for (OutputPoint point : this.getMachine().getDescriptor().get(AutomationComponent.class).getOutputs())
        {
            if (!anySlotFull(point))
                continue;

            MultiblockSide side = this.getMachine().getDescriptor().get(MultiblockComponent.class)
                    .multiblockSideToWorldSide(point.getSide(), this.getMachine().getFacing());

            BlockPos computedPos = this.getMachineTile().getPos().add(side.getPos());
            if (this.hasBelt(this.getMachineTile().getWorld(), computedPos))
            {
                int slot;

                if (point.getSlots().length > 1)
                {
                    if (point.isRoundRobin())
                        slot = this.getNextSlotSeq(point);
                    else
                        slot = this.getFirstFullSlot(point);
                }
                else
                    slot = inventory.getOutputSlots()[point.getSlots()[0]];

                if (slot == -1)
                    continue;

                ItemStack toTransfer = inventory.getStackInSlot(slot);
                IBelt belt = (IBelt) this.getMachineTile().getWorld().getTileEntity(computedPos);

                if (this.canInsert(belt, toTransfer, side.getFacing()))
                {
                    this.insert(belt,
                            this.outputWrapper.extractItem(slot, 1, false), side.getFacing());
                }
            }
        }
        this.getMachineTile().sync();
    }

    private void insert(IBelt belt, ItemStack stack, EnumFacing facing)
    {
        if (belt.getFacing() == facing.rotateY())
            belt.insert(stack, 0, 10 / 32F, true);
        if (belt.getFacing() == facing.rotateYCCW())
            belt.insert(stack, 10 / 16F, 10 / 32F, true);
        else
            belt.insert(stack, true);
    }

    private boolean canInsert(IBelt belt, ItemStack stack, EnumFacing facing)
    {
        if (belt.getFacing() == facing)
            return false;
        if (belt.getFacing() == facing.rotateY())
            return belt.insert(stack, 0, 10 / 32F, false);
        if (belt.getFacing() == facing.rotateYCCW())
            return belt.insert(stack, 10 / 16F, 10 / 32F, false);
        else
            return belt.insert(stack, false);
    }

    private boolean hasBelt(World w, BlockPos pos)
    {
        return w.getTileEntity(pos) instanceof IBelt;
    }

    private boolean anySlotFull(OutputPoint point)
    {
        for (int slot : point.getSlots())
        {
            if (!inventory.getStackInSlot(inventory.getOutputSlots()[slot]).isEmpty())
                return true;
        }
        return false;
    }

    private int getNextSlotSeq(OutputPoint point)
    {
        if (!this.lastOutput.containsKey(point))
            this.lastOutput.put(point, point.getSlots().length);

        int start = this.lastOutput.get(point);
        if (start == point.getSlots().length)
            start = -1;
        start++;

        while (start < point.getSlots().length)
        {
            if (!inventory.getStackInSlot(inventory.getOutputSlots()[point.getSlots()[start]]).isEmpty())
            {
                this.lastOutput.put(point, start);
                return inventory.getOutputSlots()[point.getSlots()[start]];
            }
            start++;

            if (start == point.getSlots().length)
                start = 0;
        }
        return 0;
    }

    private int getFirstFullSlot(OutputPoint point)
    {
        for (int slot : point.getSlots())
        {
            if (!inventory.getStackInSlot(inventory.getOutputSlots()[slot]).isEmpty())
                return inventory.getOutputSlots()[slot];
        }
        return -1;
    }
}
