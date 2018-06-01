package net.ros.common.steam;

import net.ros.common.machine.component.SteamComponent;

import java.text.NumberFormat;

public class SteamUtil
{
    public static final int BASE_PRESSURE = 1;

    public static final SteamTank EMPTY_TANK = new SteamTank(0, 0);

    public static final NumberFormat pressureFormat;

    static
    {
        pressureFormat = NumberFormat.getInstance();
        SteamUtil.pressureFormat.setMaximumFractionDigits(2);
        SteamUtil.pressureFormat.setMinimumFractionDigits(2);
    }

    public static ISteamTank createTank(SteamComponent component)
    {
        if (component == null)
            return null;
        return new SteamTank(component.getSteamCapacity(), component.getMaxPressureCapacity(),
                component.getSafePressureCapacity());
    }

    public static ISteamTank createTank(int steam, int capacity, float pressure)
    {
        ISteamTank tank = new SteamTank(capacity, pressure);
        tank.setSteam(steam);
        return tank;
    }
}
