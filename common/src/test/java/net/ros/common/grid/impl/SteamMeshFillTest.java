package net.ros.common.grid.impl;

import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.SteamTank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static net.ros.common.steam.SteamUtil.createTank;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SteamMeshFillTest
{
    @Test
    public void testSimpleFill()
    {
        ISteamTank tank1 = createTank(1000, 2000, 2);
        ISteamTank tank2 = createTank(1000, 2000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(1250);
        assertThat(tank2.getSteam()).isEqualTo(1250);

        assertThat(mesh.getSteam()).isEqualTo(2500);
    }

    @Test
    public void testUnequalFill()
    {
        ISteamTank tank1 = createTank(1000, 2000, 2);
        ISteamTank tank2 = createTank(500, 1000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(1334);
        assertThat(tank2.getSteam()).isEqualTo(666);

        assertThat(mesh.getSteam()).isEqualTo(2000);
    }

    @Test
    public void testEmptyTankSingleFill()
    {
        ISteamTank tank1 = createTank(2000, 2000, 1);
        ISteamTank tank2 = createTank(0, 1000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(2000);
        assertThat(tank2.getSteam()).isEqualTo(500);

        assertThat(mesh.getSteam()).isEqualTo(2500);
    }

    @Test
    public void testFourSameFill()
    {
        ISteamTank tank1 = createTank(1000, 2000, 2);
        ISteamTank tank2 = createTank(1000, 2000, 2);
        ISteamTank tank3 = createTank(1000, 2000, 2);
        ISteamTank tank4 = createTank(1000, 2000, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);
        mesh.addHandler(tank3);
        mesh.addHandler(tank4);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(1125);
        assertThat(tank2.getSteam()).isEqualTo(1125);
        assertThat(tank3.getSteam()).isEqualTo(1125);
        assertThat(tank4.getSteam()).isEqualTo(1125);

        assertThat(mesh.getSteam()).isEqualTo(4500);
    }

    @Test
    public void testFourUnequalFill()
    {
        ISteamTank tank1 = createTank(1000, 2000, 2);
        ISteamTank tank2 = createTank(500, 1000, 2);
        ISteamTank tank3 = createTank(50, 100, 2);
        ISteamTank tank4 = createTank(250, 500, 2);

        SteamMesh mesh = new SteamMesh();
        mesh.addHandler(tank1);
        mesh.addHandler(tank2);
        mesh.addHandler(tank3);
        mesh.addHandler(tank4);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(500);

        assertThat(tank1.getSteam()).isEqualTo(1278);
        assertThat(tank2.getSteam()).isEqualTo(639);
        assertThat(tank3.getSteam()).isEqualTo(64);
        assertThat(tank4.getSteam()).isEqualTo(319);

        assertThat(mesh.getSteam()).isEqualTo(2300);
    }

    @Test
    public void testLimitedFill()
    {
        ISteamTank tank = createTank(1000, 2000, 2);

        SteamMesh mesh = new SteamMesh(64);
        mesh.addHandler(tank);

        int filled = mesh.fillSteam(500, true);

        assertThat(filled).isEqualTo(64);

        assertThat(tank.getSteam()).isEqualTo(1000 + 64);
    }
}
