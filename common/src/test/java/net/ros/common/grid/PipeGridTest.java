package net.ros.common.grid;

import net.minecraftforge.fluids.FluidTank;
import net.ros.common.grid.impl.PipeGrid;
import net.ros.common.grid.node.IFluidPipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PipeGridTest
{
    @Before
    public void setupTest()
    {
        GridManager.getInstance().cableGrids.clear();
    }

    @Test
    public void testPipeList()
    {
        PipeGrid grid = new PipeGrid(0);
        IFluidPipe pipe = mock(IFluidPipe.class);

        when(pipe.isInput()).thenReturn(true);
        when(pipe.isOutput()).thenReturn(true);
        when(pipe.getBufferTank()).thenReturn(new FluidTank(0));

        grid.addCable(pipe);
        grid.addConnectedPipe(pipe);
        grid.tick();

        verify(pipe, times(1)).fillNeighbors();
        verify(pipe, times(1)).drainNeighbors();
    }
}
