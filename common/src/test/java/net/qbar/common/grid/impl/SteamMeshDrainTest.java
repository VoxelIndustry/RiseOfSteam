package net.qbar.common.grid.impl;

import net.qbar.common.steam.SteamTank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SteamMeshDrainTest
{
    @Test
    public void testSimpleDrain()
    {
        SteamTank tank1 = new SteamTank(1000, 2000, 2);
        SteamTank tank2 = new SteamTank(1000, 2000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int drained = mesh.drainSteam(500, true);

        assertThat(drained).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(750);
        assertThat(tank2.getSteam()).isEqualTo(750);

        assertThat(mesh.getSteam()).isEqualTo(1500);
    }

    @Test
    public void testUnequalDrain()
    {
        SteamTank tank1 = new SteamTank(1000, 2000, 2);
        SteamTank tank2 = new SteamTank(500, 1000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int drained = mesh.drainSteam(500, true);

        assertThat(drained).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(666);
        assertThat(tank2.getSteam()).isEqualTo(334);

        assertThat(mesh.getSteam()).isEqualTo(1000);
    }

    @Test
    public void testEmptyTankSingleDrain()
    {
        SteamTank tank1 = new SteamTank(1000, 2000, 2);
        SteamTank tank2 = new SteamTank(0, 1000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int drained = mesh.drainSteam(500, true);

        assertThat(drained).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(500);
        assertThat(tank2.getSteam()).isEqualTo(0);

        assertThat(mesh.getSteam()).isEqualTo(500);
    }

    @Test
    public void testFourSameDrain()
    {
        SteamTank tank1 = new SteamTank(1000, 2000, 2);
        SteamTank tank2 = new SteamTank(1000, 2000, 2);
        SteamTank tank3 = new SteamTank(1000, 2000, 2);
        SteamTank tank4 = new SteamTank(1000, 2000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);
        mesh.addHandler(tank3);
        mesh.addHandler(tank4);

        int drained = mesh.drainSteam(500, true);

        assertThat(drained).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(875);
        assertThat(tank2.getSteam()).isEqualTo(875);
        assertThat(tank3.getSteam()).isEqualTo(875);
        assertThat(tank4.getSteam()).isEqualTo(875);

        assertThat(mesh.getSteam()).isEqualTo(3500);
    }

    @Test
    public void testFourUnequalDrain()
    {
        SteamTank tank1 = new SteamTank(1000, 2000, 2);
        SteamTank tank2 = new SteamTank(500, 1000, 2);
        SteamTank tank3 = new SteamTank(50, 100, 2);
        SteamTank tank4 = new SteamTank(250, 500, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);
        mesh.addHandler(tank3);
        mesh.addHandler(tank4);

        int drained = mesh.drainSteam(500, true);

        assertThat(drained).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(722);
        assertThat(tank2.getSteam()).isEqualTo(361);
        assertThat(tank3.getSteam()).isEqualTo(36);
        assertThat(tank4.getSteam()).isEqualTo(181);

        assertThat(mesh.getSteam()).isEqualTo(1300);
    }
}
