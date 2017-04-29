package net.qbar.common.tile.machine;

import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.CraftingMachineDescriptor;
import net.qbar.common.tile.MachineDescriptor;

public class QBarMachines
{
    public static final MachineDescriptor         OFFSHORE_PUMP;

    public static final CraftingMachineDescriptor ROLLING_MILL;
    public static final CraftingMachineDescriptor FURNACE_MK1;
    public static final CraftingMachineDescriptor FURNACE_MK2;
    public static final CraftingMachineDescriptor ASSEMBLER;

    public static final CraftingMachineDescriptor ORE_WASHER;
    public static final CraftingMachineDescriptor SORTING_MACHINE;

    static
    {
        OFFSHORE_PUMP = new MachineDescriptor("offshorepump", 2000, 5, SteamUtil.AMBIANT_PRESSURE,
                1.5f * SteamUtil.AMBIANT_PRESSURE, true);

        ROLLING_MILL = new CraftingMachineDescriptor("rollingmill", QBarRecipeHandler.ROLLINGMILL_UID, 3, 1, 1, 1f,
                2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);
        FURNACE_MK1 = new CraftingMachineDescriptor("furnacemk1", QBarRecipeHandler.FURNACE_UID, 3, 1, 1, 1f, 2000, 10,
                SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);
        FURNACE_MK2 = new CraftingMachineDescriptor("furnacemk2", QBarRecipeHandler.FURNACE_UID, 3, 1, 1, 3f, 2000, 10,
                SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);
        ASSEMBLER = new CraftingMachineDescriptor("assembler", "", 3, 1, 1, 1f, 2000, 10,
                SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);

        ORE_WASHER = new CraftingMachineDescriptor("orewasher", QBarRecipeHandler.ORE_WASHER_UID, 5, 1, 3, 1f, 2000, 10,
                SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);
        SORTING_MACHINE = new CraftingMachineDescriptor("sortingmachine", QBarRecipeHandler.SORTING_MACHINE_UID, 3, 1, 1, 1f, 2000, 10,
                SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true);
    }
}