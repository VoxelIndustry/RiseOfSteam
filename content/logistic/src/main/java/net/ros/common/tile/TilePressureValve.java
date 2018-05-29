package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.grid.node.PipeType;
import net.ros.common.steam.SteamTank;

public class TilePressureValve extends TileSteamPipe
{
    @Getter
    @Setter
    private float fillPressureLimit;
    @Getter
    @Setter
    private float drainPressureLimit;

    public TilePressureValve(PipeType type, int transferCapacity, float maxPressure)
    {
        super(type, transferCapacity, maxPressure);

        this.fillPressureLimit = -1;
        this.drainPressureLimit = -1;
    }

    public TilePressureValve()
    {
        this(null, 0, 0);
    }

    @Override
    protected SteamTank createSteamTank(int capacity, float maxPressure)
    {
        return new SteamTank(capacity, maxPressure)
        {
            private TilePressureValve valve;

            {
                this.valve = TilePressureValve.this;
            }

            @Override
            public int drainSteam(int amount, boolean doDrain)
            {
                if (valve.getDrainPressureLimit() != -1)
                {
                    if (this.getPressure() <= valve.getDrainPressureLimit())
                        return 0;
                    if ((this.getSteam() - amount / this.getCapacity()) < valve.getDrainPressureLimit())
                        return super.drainSteam(-this.getSteamDifference(valve.getDrainPressureLimit()), doDrain);
                }
                return super.drainSteam(amount, doDrain);
            }

            @Override
            public int fillSteam(int amount, boolean doFill)
            {
                if (valve.getFillPressureLimit() != -1)
                {
                    if (this.getPressure() >= valve.getFillPressureLimit())
                        return 0;
                    if (((this.getSteam() + amount) / this.getCapacity()) > valve.getFillPressureLimit())
                        return super.fillSteam(this.getSteamDifference(valve.getFillPressureLimit()), doFill);
                }
                return super.fillSteam(amount, doFill);
            }
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.fillPressureLimit = tag.getFloat("fillPressureLimit");
        this.drainPressureLimit = tag.getFloat("drainPressureLimit");

        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("fillPressureLimit", this.fillPressureLimit);
        tag.setFloat("drainPressureLimit", this.drainPressureLimit);

        return super.writeToNBT(tag);
    }
}
