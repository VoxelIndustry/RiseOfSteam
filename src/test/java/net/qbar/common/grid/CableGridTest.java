package net.qbar.common.grid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CableGridTest
{
    @Test
    public void testCableHandling()
    {
        CableGrid grid = new CableGridTestImpl(0);
        ITileCable cable = new ITileCableTestImpl();

        grid.addCable(cable);

        assertThat(grid.hasCable(cable)).isTrue();
        assertThat(grid.removeCable(cable)).isTrue();
        assertThat(grid.removeCable(cable)).isFalse();
    }

    @Test
    public void testBasicGrid()
    {
        CableGrid grid = new CableGridTestImpl(0);
        CableGrid copy = grid.copy(0);

        assertThat(grid.canMerge(grid)).isFalse();
        assertThat(grid.equals(copy)).isTrue();
        assertThat(grid.equals(null)).isFalse();
        assertThat(grid.equals(0)).isFalse();
    }
}
