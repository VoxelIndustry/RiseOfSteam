package net.ros.common.grid.impl;

import lombok.Getter;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ros.common.grid.node.IFluidPipe;
import net.ros.common.grid.node.IPipeValve;
import net.ros.common.grid.node.ITileNode;
import net.ros.common.grid.node.PipeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.ros.common.util.FluidUtils.getFluidDifference;
import static net.ros.common.util.FluidUtils.getTankPressure;

@Getter
public class PipeGrid extends CableGrid
{
    private final List<IFluidPipe> outputs;
    private final List<IFluidPipe> inputs;

    public PipeGrid(final int identifier)
    {
        super(identifier);

        this.outputs = new ArrayList<>();
        this.inputs = new ArrayList<>();
    }

    @Override
    public void addCable(@Nonnull final ITileNode cable)
    {
        super.addCable(cable);
    }

    @Override
    public boolean removeCable(final ITileNode cable)
    {
        return super.removeCable(cable);
    }

    @Override
    public void tick()
    {
        if (!this.getInputs().isEmpty())
            this.getInputs().forEach(IFluidPipe::drainNeighbors);

        List<ITileNode> toDestroy = new ArrayList<>();

        for (ITileNode<?> node: this.getCables())
        {
            IFluidPipe pipe = (IFluidPipe) node;

            if ((pipe instanceof IPipeValve && !((IPipeValve) pipe).isOpen()) ||
                    (pipe.getBufferTank().getFluidAmount() == 0))
                continue;

            IFluidTank tank = pipe.getBufferTank();
            int transferred = 0;

            if (tank.getFluid().getFluid().getTemperature() > PipeType.getHeat(pipe.getType()))
                toDestroy.add(node);

            List<EnumFacing> randConnections = new ArrayList<>(pipe.getConnectionsMap().keySet());
            Collections.shuffle(randConnections, node.getBlockWorld().rand);

            for (EnumFacing facing: randConnections)
            {
                IFluidPipe otherPipe = (IFluidPipe) pipe.getConnected(facing);
                FluidStack pipeFluid = pipe.getBufferTank().getFluid();
                FluidStack otherFluid = otherPipe.getBufferTank().getFluid();

                if (pipeFluid != null && otherFluid != null && !pipeFluid.equals(otherFluid))
                    continue;

                transferred += balanceTanks(tank, otherPipe.getBufferTank(), pipe.getTransferRate() - transferred);

                if (transferred >= pipe.getTransferRate())
                    break;
            }
        }

        toDestroy.forEach(node -> node.getBlockWorld().setBlockState(node.getBlockPos(),
                Blocks.FIRE.getDefaultState()));

        if (!this.getOutputs().isEmpty())
            this.getOutputs().forEach(IFluidPipe::fillNeighbors);
    }

    private int balanceTanks(IFluidTank first, IFluidTank second, int transferRate)
    {
        float average = (getTankPressure(second) + getTankPressure(first)) / 2;

        if (getTankPressure(second) < average)
        {
            FluidStack drained = first.drain(Math.min(transferRate, getFluidDifference(first, average)), false);
            int filled = second.fill(drained, false);

            if (filled < 1)
                return 0;

            return second.fill(first.drain(filled, true), true);
        }
        return 0;
    }

    @Override
    public CableGrid copy(final int identifier)
    {
        return new PipeGrid(identifier);
    }

    @Override
    public boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof PipeGrid)
            return super.canMerge(grid);
        return false;
    }

    @Override
    public void onMerge(final CableGrid grid)
    {
        this.getOutputs().addAll(((PipeGrid) grid).getOutputs());
        this.getInputs().addAll(((PipeGrid) grid).getInputs());
    }

    @Override
    public void onSplit(final CableGrid grid)
    {
        this.getOutputs().addAll(
                ((PipeGrid) grid).getOutputs().stream().filter(this.getCables()::contains)
                        .collect(Collectors.toList()));
        this.getInputs().addAll(
                ((PipeGrid) grid).getInputs().stream().filter(this.getCables()::contains).collect(Collectors.toList()));
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
