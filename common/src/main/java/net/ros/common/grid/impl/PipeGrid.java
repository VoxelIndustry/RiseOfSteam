package net.ros.common.grid.impl;

import lombok.Getter;
import net.minecraftforge.fluids.Fluid;
import net.ros.common.fluid.LimitedTank;
import net.ros.common.grid.node.IFluidPipe;
import net.ros.common.grid.node.ITileNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PipeGrid extends CableGrid
{
    private final LimitedTank tank;
    private final int         transferCapacity;

    private final List<IFluidPipe> outputs;
    private final List<IFluidPipe> inputs;

    public PipeGrid(final int identifier, final int transferCapacity)
    {
        super(identifier);

        this.transferCapacity = transferCapacity;
        this.tank = new LimitedTank(transferCapacity * 4, transferCapacity);

        this.outputs = new ArrayList<>();
        this.inputs = new ArrayList<>();
    }

    @Override
    public void addCable(@Nonnull final ITileNode cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getCapacity());
    }

    @Override
    public boolean removeCable(final ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.getTank().setCapacity(this.getCapacity());

            if (this.getTank().getFluidAmount() > 0)
                this.getTank().drainInternal(this.getTank().getFluidAmount() / (this.getCables().size() + 1), true);
            this.outputs.remove(cable);
            this.inputs.remove(cable);
            return true;
        }
        return false;
    }

    @Override
    public void tick()
    {
        if (!this.getInputs().isEmpty())
            this.getInputs().forEach(IFluidPipe::drainNeighbors);
        if (!this.getOutputs().isEmpty())
            this.getOutputs().forEach(IFluidPipe::fillNeighbors);
    }

    @Override
    public CableGrid copy(final int identifier)
    {
        return new PipeGrid(identifier, this.transferCapacity);
    }

    @Override
    public boolean canMerge(final CableGrid grid)
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
    public void onMerge(final CableGrid grid)
    {
        this.getTank().setCapacity(this.getCapacity());
        this.getOutputs().addAll(((PipeGrid) grid).getOutputs());
        this.getInputs().addAll(((PipeGrid) grid).getInputs());
        if (((PipeGrid) grid).getTank().getFluid() != null)
            this.getTank().fillInternal(((PipeGrid) grid).getTank().getFluid(), true);
    }

    @Override
    public void onSplit(final CableGrid grid)
    {
        this.getOutputs().addAll(
                ((PipeGrid) grid).getOutputs().stream().filter(this.getCables()::contains)
                        .collect(Collectors.toList()));
        this.getInputs().addAll(
                ((PipeGrid) grid).getInputs().stream().filter(this.getCables()::contains).collect(Collectors.toList()));
        this.getTank().fillInternal(((PipeGrid) grid).getTank().drainInternal(
                ((PipeGrid) grid).getTank().getFluidAmount() / grid.getCables().size() * this.getCables().size(),
                false), true);
    }

    public Fluid getFluid()
    {
        return this.getTank().getFluidType();
    }

    public int getCapacity()
    {
        if (this.getCables().size() < 4)
            return 4 * this.getTransferCapacity();
        return this.getCables().size() * this.getTransferCapacity();
    }

    public boolean isEmpty()
    {
        return this.getFluid() == null || this.getTank().getFluidAmount() == 0;
    }

    public void addConnectedPipe(IFluidPipe pipe)
    {
        if (pipe.isInput())
            this.inputs.add(pipe);
        if (pipe.isOutput())
            this.outputs.add(pipe);
    }

    public void removeConnectedPipe(IFluidPipe pipe)
    {
        if (pipe.isInput())
            this.inputs.remove(pipe);
        if (pipe.isOutput())
            this.outputs.remove(pipe);
    }
}
