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

    private final HashSet<ITileCable<?>> cables;

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
        if (grid.getIdentifier() != this.getIdentifier())
            return true;
        return false;
    }

    /**
     * Called on the destination grid after the merging has occurred.
     *
     * @param the source grid
     */
    void onMerge(final CableGrid grid)
    {

    }

    /**
     * Called after a grid splitting has occurred, each new fragment will
     * receive this event.
     *
     * @param the grid source grid before splitting.
     */
    void onSplit(final CableGrid grid)
    {

    }

    public void addCable(@Nonnull final ITileCable<?> cable)
    {
        this.cables.add(cable);
    }

    public void addCables(final Collection<ITileCable<?>> cables)
    {
        cables.forEach(this::addCable);
    }

    public boolean removeCable(final ITileCable<?> cable)
    {
        if (this.cables.remove(cable))
        {
            if (this.cables.isEmpty())
                GridManager.getInstance().removeGrid(this.identifier);
            return true;
        }
        return false;
    }

    public void removeCables(final Collection<ITileCable<?>> cables)
    {
        cables.forEach(this::removeCable);
    }

    public boolean hasCable(final ITileCable<?> cable)
    {
        return this.cables.contains(cable);
    }

    public int getIdentifier()
    {
        return this.identifier;
    }

    public HashSet<ITileCable<?>> getCables()
    {
        return this.cables;
    }
}
