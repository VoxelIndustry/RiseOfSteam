package net.ros.common.tile.module;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.machine.module.IModularMachine;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.machine.module.ISerializableModule;
import net.ros.common.machine.module.ITickableModule;
import net.ros.common.machine.module.MachineModule;
import net.ros.common.machine.module.impl.FluidStorageModule;

@Getter
public class SteamBoilerModule extends MachineModule implements ITickableModule, ISerializableModule
{
    @Setter
    private float maxHeat;
    @Setter
    private float currentHeat;

    private final String waterTank;

    @Builder
    public SteamBoilerModule(IModularMachine machine, String waterTank, float maxHeat)
    {
        super(machine, "SteamBoilerModule");

        this.maxHeat = maxHeat;
        this.waterTank = waterTank;
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
            this.sync();
        }

        if (this.currentHeat >= 100)
        {
            int toProduce = (int) (1 / Math.E * (this.currentHeat / 10));

            FluidStack drained = this.getMachine().getModule(FluidStorageModule.class)
                    .getFluidHandler(this.getWaterTank()).drain(toProduce, true);
            if (drained != null)
                toProduce = drained.amount;
            else
                toProduce = 0;

            this.getMachine().getModule(SteamModule.class).getInternalSteamHandler().fillSteam(toProduce, true);
            if (toProduce != 0 && this.getMachineTile().getWorld().getTotalWorldTime() % 2 == 0)
                this.currentHeat -= 0.075;
            this.sync();
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

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.currentHeat = tag.getFloat("currentHeat");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setFloat("currentHeat", this.currentHeat);
        return tag;
    }

    public void addHeat(float heat)
    {
        this.currentHeat += heat;
    }
}
