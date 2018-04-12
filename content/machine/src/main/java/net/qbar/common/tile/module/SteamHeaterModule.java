package net.qbar.common.tile.module;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ISerializableModule;
import net.qbar.common.machine.module.ITickableModule;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.steam.ISteamTank;

@Getter
public class SteamHeaterModule extends MachineModule implements ISerializableModule, ITickableModule
{
    @Setter
    private float maxHeat;
    @Setter
    private float currentHeat;
    private float heatPerTick;
    private int   steamPerHeat;

    private float tempSteam;

    @Builder
    public SteamHeaterModule(IModularMachine machine, float maxHeat, float heatPerTick, int steamPerHeat)
    {
        super(machine, "SteamHeaterModule");

        this.maxHeat = maxHeat;
        this.heatPerTick = heatPerTick;
        this.steamPerHeat = steamPerHeat;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.currentHeat = tag.getFloat("currentHeat");
        this.tempSteam = tag.getFloat("tempSteam");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setFloat("currentHeat", this.currentHeat);
        tag.setFloat("tempSteam", this.tempSteam);
        return tag;
    }

    @Override
    public void tick()
    {
        if (this.getMachineTile().getWorld().getTotalWorldTime() % 5 == 0)
        {
            if (this.currentHeat > this.getMinimumTemp())
                this.currentHeat -= 0.1f;
            else if (this.currentHeat < this.getMinimumTemp())
                this.currentHeat = this.getMinimumTemp();
        }

        ISteamTank tank = this.getMachine().getModule(SteamModule.class).getInternalSteamHandler();

        if (tank.getSteam() > steamPerHeat)
        {
            float heatToProduce = Math.min(maxHeat - currentHeat, heatPerTick);

            this.tempSteam += heatToProduce * steamPerHeat;
            int drained = 0;

            if (tempSteam > 1)
                drained = tank.drainSteam((int) tempSteam, true);

            this.tempSteam -= drained;
            this.currentHeat += (float) drained / steamPerHeat;
        }
    }

    private int getMinimumTemp()
    {
        return (int) (this.getMachineTile().getWorld().getBiome(
                this.getMachineTile().getPos()).getTemperature(this.getMachineTile().getPos()) * 20);
    }

    public int getHeatScaled(int pixels)
    {
        final int i = (int) this.getMaxHeat();

        if (i == 0)
            return -1;

        return (int) Math.min(this.getCurrentHeat() * pixels / i, pixels);
    }
}
