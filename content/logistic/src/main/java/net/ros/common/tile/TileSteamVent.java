package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.machine.Machines;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.SteamTank;
import net.ros.common.tile.machine.TileModularMachine;

public class TileSteamVent extends TileModularMachine
{
    @Getter
    @Setter
    private float ventPressure;

    public TileSteamVent()
    {
        super(Machines.STEAM_VENT);

        this.setVentPressure(0.5f);
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new SteamModule(this, this::createTank));
        this.addModule(new IOModule(this));
    }

    private ISteamTank createTank(SteamComponent steamComponent)
    {
        return new SteamVentTank(this, 0, steamComponent.getSteamCapacity(), steamComponent.getMaxPressureCapacity());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.setVentPressure(tag.getFloat("ventPressure"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("ventPressure", this.getVentPressure());

        return super.writeToNBT(tag);
    }

    private class SteamVentTank extends SteamTank
    {
        private TileSteamVent vent;

        SteamVentTank(TileSteamVent vent, int steamAmount, int capacity, float maxPressure)
        {
            super(steamAmount, capacity, maxPressure);

            this.vent = vent;
        }

        @Override
        public int getSteam()
        {
            return (int) (vent.getVentPressure() * this.getCapacity());
        }

        @Override
        public void setSteam(int steam)
        {
            // Void everything
        }

        @Override
        public int drainInternal(int amount, boolean doDrain)
        {
            return 0;
        }
    }
}
