package net.qbar.common.tile.machine;

import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.steam.SteamUtil;

public class TileSteamMeshTest extends TileModularMachine
{
    public TileSteamMeshTest()
    {
        super(QBarMachines.STEAM_MESH_TEST);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, SteamUtil::createTank));
    }
}
