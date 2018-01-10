package net.qbar.common.grid;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.EnumMap;

@Getter
public class WorkshopGrid extends CableGrid
{
    private final EnumMap<WorkshopMachine, ITileCable<?>> machines;

    public WorkshopGrid(int identifier)
    {
        super(identifier);

        this.machines = new EnumMap<>(WorkshopMachine.class);
    }

    @Override
    public void addCable(@Nonnull final ITileCable<?> cable)
    {
        super.addCable(cable);

        machines.put(((ITileWorkshop) cable).getType(), cable);
    }

    @Override
    public boolean removeCable(ITileCable<?> cable)
    {
        machines.remove(((ITileWorkshop) cable).getType());
        return super.removeCable(cable);
    }

    @Override
    boolean canMerge(CableGrid grid)
    {
        return super.canMerge(grid) &&
                ((WorkshopGrid) grid).getMachines().keySet().stream().noneMatch(machines::containsKey);
    }

    @Override
    CableGrid copy(int identifier)
    {
        return new WorkshopGrid(identifier);
    }
}
