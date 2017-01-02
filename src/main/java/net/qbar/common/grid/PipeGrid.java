package net.qbar.common.grid;

import java.util.HashSet;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.Fluid;
import net.qbar.common.fluid.LimitedTank;

public class PipeGrid extends CableGrid
{

    private final LimitedTank         tank;
    private int                       transferCapacity;

    private final HashSet<IFluidPipe> outputs;

    public PipeGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);

        this.transferCapacity = transferCapacity;
        this.tank = new LimitedTank("PipeGrid", transferCapacity * 4, transferCapacity);

        this.outputs = new HashSet<>();
    }

    @Override
    public void addCable(@Nonnull final ITileCable cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getCapacity());
    }

    @Override
    public boolean removeCable(final ITileCable cable)
    {
        final boolean rtn = super.removeCable(cable);

        this.getTank().setCapacity(this.getCapacity());
        return rtn;
    }

    public int getTransferCapacity()
    {
        return this.transferCapacity;
    }

    @Override
    public void tick()
    {
        if (!this.getOutputs().isEmpty())
            this.getOutputs().forEach(pipe -> pipe.fillNeighbors());
    }

    @Override
    void dirtyPass()
    {

    }

    @Override
    void applyData(final CableGrid grid)
    {
        if (grid instanceof PipeGrid)
        {
            this.transferCapacity = ((PipeGrid) grid).getTransferCapacity();
        }
    }

    @Override
    boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof PipeGrid)
        {
            if (((PipeGrid) grid).getFluid() == null || this.getFluid() == null
                    || ((PipeGrid) grid).getFluid().equals(this.getFluid()))
                return super.canMerge(grid);
        }
        return false;
    }

    @Override
    void onMerge(final CableGrid grid)
    {
        this.getTank().setCapacity(this.getCapacity());
        this.getTank().fill(((PipeGrid) grid).getTank().getFluid(), true);
    }

    @Override
    void onSplit(final CableGrid grid)
    {
        this.getTank().fill(((PipeGrid) grid).getTank().drain(this.getCapacity(), true), true);
    }

    public Fluid getFluid()
    {
        return this.getTank().getFluidType();
    }

    public LimitedTank getTank()
    {
        return this.tank;
    }

    public int getCapacity()
    {
        return Math.max(this.getTransferCapacity(), this.getCables().size() * (this.getTransferCapacity() / 4));
    }

    public HashSet<IFluidPipe> getOutputs()
    {
        return this.outputs;
    }

    public void addOutput(final IFluidPipe output)
    {
        this.outputs.add(output);
    }

    public void removeOutput(final IFluidPipe output)
    {
        this.outputs.remove(output);
    }
}
