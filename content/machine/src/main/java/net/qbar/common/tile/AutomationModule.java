package net.qbar.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.grid.IBelt;
import net.qbar.common.machine.AutomationComponent;
import net.qbar.common.machine.OutputPoint;
import net.qbar.common.multiblock.MultiblockSide;

import java.util.HashMap;

public class AutomationModule
{
    private final AutomationComponent component;

    private final HashMap<OutputPoint, Integer> lastOutput;

    AutomationModule(AutomationComponent component)
    {
        this.component = component;
        this.lastOutput = new HashMap<>();
    }

    public boolean tick(World world, BlockPos pos, TileCraftingMachineBase machine)
    {
        boolean isDirty = false;

        for (OutputPoint point : this.component.getOutputs())
        {
            if (!anySlotFull(point, machine))
                continue;

            MultiblockSide side = machine.getMultiblock().multiblockSideToWorldSide(
                    point.getSide(), machine.getFacing());
            BlockPos computedPos = pos.add(side.getPos());
            if (this.hasBelt(world, computedPos))
            {
                int slot;

                if (point.getSlots().length > 1)
                {
                    if (point.isRoundRobin())
                        slot = this.getNextSlotSeq(point, machine);
                    else
                        slot = this.getFirstFullSlot(point, machine);
                }
                else
                    slot = machine.getOutputSlots()[point.getSlots()[0]];

                if (slot == -1)
                    continue;

                ItemStack toTransfer = machine.getStackInSlot(slot);
                IBelt belt = (IBelt) world.getTileEntity(computedPos);

                if (this.canInsert(belt, toTransfer, side.getFacing()))
                {
                    this.insert(belt,
                            machine.getInventoryWrapper(EnumFacing.NORTH).extractItem(slot, 1, false),
                            side.getFacing());
                    isDirty = true;
                }
            }
        }
        return isDirty;
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

    private boolean anySlotFull(OutputPoint point, TileCraftingMachineBase machine)
    {
        for (int slot : point.getSlots())
        {
            if (!machine.getStackInSlot(machine.getOutputSlots()[slot]).isEmpty())
                return true;
        }
        return false;
    }

    private int getNextSlotSeq(OutputPoint point, TileCraftingMachineBase machine)
    {
        if (!this.lastOutput.containsKey(point))
            this.lastOutput.put(point, point.getSlots().length);

        int start = this.lastOutput.get(point);
        if (start == point.getSlots().length)
            start = -1;
        start++;

        while (start < point.getSlots().length)
        {
            if (!machine.getStackInSlot(machine.getOutputSlots()[point.getSlots()[start]]).isEmpty())
            {
                this.lastOutput.put(point, start);
                return machine.getOutputSlots()[point.getSlots()[start]];
            }
            start++;

            if (start == point.getSlots().length)
                start = 0;
        }
        return 0;
    }

    private int getFirstFullSlot(OutputPoint point, TileCraftingMachineBase machine)
    {
        for (int slot : point.getSlots())
        {
            if (!machine.getStackInSlot(machine.getOutputSlots()[slot]).isEmpty())
                return machine.getOutputSlots()[slot];
        }
        return -1;
    }
}
