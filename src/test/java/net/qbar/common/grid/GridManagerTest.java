package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GridManagerTest
{
    @Before
    public void setupTest()
    {
        GridManager.getInstance().cableGrids.clear();
    }

    @Test
    public void testGridList()
    {
        CableGrid grid = new CableGrid(GridManager.getInstance().getNextID())
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        };
        GridManager.getInstance().addGrid(grid);

        assertThat(grid.getIdentifier()).isEqualTo(0);
        assertThat(GridManager.getInstance().getNextID()).isEqualTo(1);
        assertThat(GridManager.getInstance().hasGrid(0)).isTrue();
        assertThat(GridManager.getInstance().getGrid(0)).isEqualTo(grid);
        assertThat(GridManager.getInstance().getGrid(1)).isNull();
    }

    @Test
    public void testGridAutoRemoval()
    {
        CableGrid grid = new CableGrid(GridManager.getInstance().getNextID())
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        };
        GridManager.getInstance().addGrid(grid);

        ITileCable cable = mock(ITileCable.class);
        grid.addCable(cable);

        assertThat(GridManager.getInstance().cableGrids).containsValue(grid);

        grid.removeCable(cable);

        assertThat(GridManager.getInstance().cableGrids).doesNotContainValue(grid);
    }

    @Test
    public void testGridTick()
    {
        CableGrid grid = mock(CableGrid.class);
        when(grid.getIdentifier()).thenReturn(0);

        GridManager.getInstance().addGrid(grid);
        GridManager.getInstance().tickGrids();
        verify(grid, times(1)).tick();
    }

    @Test
    public void testCableConnect()
    {
        ITileCable upperCable = mock(ITileCable.class);

        ITileCable cable = mock(ITileCable.class);
        when(cable.getGrid()).thenReturn(-1);
        when(cable.getConnections()).thenReturn(new EnumFacing[0]);
        when(cable.createGrid(anyInt())).then(answer -> new CableGrid(answer.getArgument(0))
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        });
        when(cable.canConnect(any())).thenReturn(true);

        GridManager.getInstance().connectCable(cable);
        assertThat(GridManager.getInstance().cableGrids).hasSize(1);

        GridManager.getInstance().getGrid(0).addCable(upperCable);
        GridManager.getInstance().getGrid(0).removeCable(cable);
        when(cable.getConnections()).thenReturn(new EnumFacing[]{EnumFacing.UP});
        when(cable.getConnected(EnumFacing.UP)).thenReturn(upperCable);
        when(upperCable.getGrid()).thenReturn(0);

        GridManager.getInstance().connectCable(cable);

        assertThat(GridManager.getInstance().getGrid(0).getCables()).hasSize(2);
        assertThat(GridManager.getInstance().getGrid(0).getCables()).contains(cable, upperCable);
    }

    @Test
    public void testCableMerge()
    {
        ITileCable center = spy(ITileCableTestImpl.class);
        ITileCable left = spy(ITileCableTestImpl.class);
        ITileCable right = spy(ITileCableTestImpl.class);

        when(center.getConnections()).thenReturn(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST});
        when(center.getConnected(EnumFacing.WEST)).thenReturn(left);
        when(center.getConnected(EnumFacing.EAST)).thenReturn(right);

        CableGrid leftGrid = new CableGrid(0)
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        };

        CableGrid rightGrid = new CableGrid(1)
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        };

        GridManager.getInstance().addGrid(leftGrid);
        GridManager.getInstance().addGrid(rightGrid);

        leftGrid.addCable(left);
        left.setGrid(leftGrid.getIdentifier());
        rightGrid.addCable(right);
        right.setGrid(rightGrid.getIdentifier());

        GridManager.getInstance().connectCable(center);

        assertThat(GridManager.getInstance().cableGrids).hasSize(1);
        assertThat(left.getGrid()).isEqualTo(center.getGrid());
        assertThat(right.getGrid()).isEqualTo(center.getGrid());
    }

    @Test
    public void testCableDisconnect()
    {
        ITileCable cable = spy(ITileCableTestImpl.class);

        cable.setGrid(GridManager.getInstance().addGrid(new CableGrid(0)
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        }).getIdentifier());

        GridManager.getInstance().disconnectCable(cable);

        assertThat(GridManager.getInstance().cableGrids).isEmpty();

        ITileCable neighbor = spy(ITileCableTestImpl.class);

        CableGrid grid = GridManager.getInstance().addGrid(new CableGrid(0)
        {
            @Override
            CableGrid copy(int identifier)
            {
                return null;
            }
        });

        grid.addCable(cable);
        grid.addCable(neighbor);
        cable.setGrid(0);
        neighbor.setGrid(0);

        when(cable.getConnections()).thenReturn(new EnumFacing[]{EnumFacing.UP});
        when(cable.getConnected(EnumFacing.UP)).thenReturn(neighbor);

        GridManager.getInstance().disconnectCable(cable);

        assertThat(GridManager.getInstance().cableGrids).hasSize(1);
        verify(neighbor, times(1)).disconnect(EnumFacing.DOWN);
    }

    @Test
    public void testGridSplit()
    {
        ITileCable center = spy(ITileCableTestImpl.class);
        ITileCable left = spy(ITileCableTestImpl.class);
        ITileCable right = spy(ITileCableTestImpl.class);
        ITileCable rightDangling = spy(ITileCableTestImpl.class);

        center.connect(EnumFacing.WEST, left);
        center.connect(EnumFacing.EAST, right);

        left.connect(EnumFacing.EAST, center);
        right.connect(EnumFacing.WEST, center);
        right.connect(EnumFacing.UP, rightDangling);

        CableGrid grid = new CableGridTestImpl(0);

        GridManager.getInstance().addGrid(grid);

        grid.addCable(left);
        left.setGrid(grid.getIdentifier());
        grid.addCable(right);
        right.setGrid(grid.getIdentifier());
        grid.addCable(center);
        center.setGrid(grid.getIdentifier());
        grid.addCable(rightDangling);
        rightDangling.setGrid(grid.getIdentifier());

        GridManager.getInstance().disconnectCable(center);

        assertThat(GridManager.getInstance().cableGrids).hasSize(2);
        assertThat(left.getGrid()).isNotEqualTo(right.getGrid());
        assertThat(right.getGrid()).isEqualTo(rightDangling.getGrid());
    }
}
