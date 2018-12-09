package net.ros.common.heat;

public interface IHeatHandler
{
    int drainHeat(int amount, boolean doDrain);

    int fillHeat(int amount, boolean doFill);

    int getHeat();

    int getCapacity();

    default int getFreeHeat()
    {
        return this.getCapacity() - this.getHeat();
    }
}
