package net.qbar.common.grid;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;

@EqualsAndHashCode
@ToString
public abstract class CableGrid
{
    private final int identifier;

    private final HashSet<ITileNode<?>> cables;

    public CableGrid(final int identifier)
    {
        this.identifier = identifier;

        this.cables = new HashSet<>();
    }

    void tick()
    {

    }

    abstract CableGrid copy(final int identifier);

    boolean canMerge(final CableGrid grid)
    {
        return grid.getIdentifier() != this.getIdentifier();
    }

    /**
     * Called on the destination grid after the merging has occurred.
     *
     * @param grid the source grid
     */
    void onMerge(final CableGrid grid)
    {

    }

    /**
     * Called after a grid splitting has occurred, each new fragment will
     * receive this event.
     *
     * @param grid the grid source grid before splitting.
     */
    void onSplit(final CableGrid grid)
    {

    }

    public void addCable(@Nonnull final ITileNode<?> cable)
    {
        this.cables.add(cable);
    }

    public void addCables(final Collection<ITileNode<?>> cables)
    {
        cables.forEach(this::addCable);
    }

    public boolean removeCable(final ITileNode<?> cable)
    {
        if (this.cables.remove(cable))
        {
            if (this.cables.isEmpty())
                GridManager.getInstance().removeGrid(this.identifier);
            return true;
        }
        return false;
    }

    public void removeCables(final Collection<ITileNode<?>> cables)
    {
        cables.forEach(this::removeCable);
    }

    public boolean hasCable(final ITileNode<?> cable)
    {
        return this.cables.contains(cable);
    }

    public int getIdentifier()
    {
        return this.identifier;
    }

    public HashSet<ITileNode<?>> getCables()
    {
        return this.cables;
    }
}
