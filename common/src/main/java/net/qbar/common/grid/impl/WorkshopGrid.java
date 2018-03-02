package net.qbar.common.grid.impl;

import lombok.Getter;
import net.qbar.common.grid.node.ITileNode;
import net.qbar.common.grid.node.ITileWorkshop;
import net.qbar.common.grid.WorkshopMachine;

import javax.annotation.Nonnull;
import java.util.EnumMap;

@Getter
public class WorkshopGrid extends CableGrid
{
    private final EnumMap<WorkshopMachine, ITileWorkshop> machines;

    public WorkshopGrid(int identifier)
    {
        super(identifier);

        this.machines = new EnumMap<>(WorkshopMachine.class);
    }

    @Override
    public void addCable(@Nonnull final ITileNode<?> cable)
    {
        super.addCable(cable);

        machines.put(((ITileWorkshop) cable).getType(), (ITileWorkshop) cable);

        WorkshopMachine type = ((ITileWorkshop) cable).getType();
        if (type == WorkshopMachine.WORKBENCH
                && this.machines.containsKey(WorkshopMachine.CARDLIBRARY))
            ((ITileWorkshop) cable).refreshWorkbenchCrafts();
        else if (this.machines.containsKey(WorkshopMachine.WORKBENCH) &&
                (type == WorkshopMachine.CARDLIBRARY ||
                        (this.machines.containsKey(WorkshopMachine.CARDLIBRARY) && type == WorkshopMachine.STORAGE)))
            this.getMachines().get(WorkshopMachine.WORKBENCH).refreshWorkbenchCrafts();
    }

    @Override
    public boolean removeCable(ITileNode<?> cable)
    {
        machines.remove(((ITileWorkshop) cable).getType());

        if (this.machines.containsKey(WorkshopMachine.WORKBENCH) &&
                (((ITileWorkshop) cable).getType() == WorkshopMachine.STORAGE ||
                        ((ITileWorkshop) cable).getType() == WorkshopMachine.CARDLIBRARY))
            this.machines.get(WorkshopMachine.WORKBENCH).refreshWorkbenchCrafts();
        return super.removeCable(cable);
    }

    @Override
    public boolean canMerge(CableGrid grid)
    {
        return super.canMerge(grid) &&
                ((WorkshopGrid) grid).getMachines().keySet().stream().noneMatch(machines::containsKey);
    }

    @Override
    public CableGrid copy(int identifier)
    {
        return new WorkshopGrid(identifier);
    }
}
