package net.qbar.common.grid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BeltGridTest
{
    @Before
    public void setupTest()
    {
        GridManager.getInstance().cableGrids.clear();
    }

    @Test
    public void testBeltList()
    {
        BeltGrid grid = new BeltGrid(0, 0.25f);
        IBelt belt = mock(IBelt.class);

        grid.addCable(belt);

        verify(belt, times(1)).setWorking(anyBoolean());

        assertThat(grid.getTank().getCapacity()).isEqualTo(256);

        grid.addCable(mock(IBelt.class));
        grid.addCable(mock(IBelt.class));
        grid.addCable(mock(IBelt.class));
        grid.addCable(mock(IBelt.class));

        assertThat(grid.getTank().getCapacity()).isEqualTo(256 + 64);

        grid.removeCable(belt);

        assertThat(grid.getTank().getCapacity()).isEqualTo(256);
    }
}
