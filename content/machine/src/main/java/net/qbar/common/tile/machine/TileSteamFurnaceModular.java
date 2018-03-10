package net.qbar.common.tile.machine;

import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.impl.CraftingInventoryModule;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.steam.SteamUtil;

public class TileSteamFurnaceModular extends TileTickingModularMachine
{
    public TileSteamFurnaceModular()
    {
        super(QBarMachines.FURNACE_MK1);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new CraftingInventoryModule(this));
        this.addModule(new CraftingModule(this));
    }
}
