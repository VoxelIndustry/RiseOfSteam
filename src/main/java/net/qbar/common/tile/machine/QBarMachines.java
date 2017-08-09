package net.qbar.common.tile.machine;

import net.minecraftforge.fluids.Fluid;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.CraftingMachineDescriptor;
import net.qbar.common.tile.MachineDescriptor;

public class QBarMachines
{
    public static final MachineDescriptor OFFSHORE_PUMP;
    public static final MachineDescriptor SMALL_MINING_DRILL;

    public static final CraftingMachineDescriptor ROLLING_MILL;
    public static final CraftingMachineDescriptor FURNACE_MK1;
    public static final CraftingMachineDescriptor FURNACE_MK2;
    public static final CraftingMachineDescriptor ASSEMBLER;

    public static final CraftingMachineDescriptor ORE_WASHER;
    public static final CraftingMachineDescriptor SORTING_MACHINE;

    static
    {
        OFFSHORE_PUMP = MachineDescriptor.builder().name("offshorepump")
                .steamCapacity(2000).steamConsumption(5)
                .workingPressure(SteamUtil.AMBIANT_PRESSURE).maxPressureCapacity(1.5f * SteamUtil.AMBIANT_PRESSURE)
                .allowOvercharge(true).build();

        SMALL_MINING_DRILL = MachineDescriptor.builder().name("smallminingdrill")
                .steamCapacity(2000).steamConsumption(25)
                .workingPressure(SteamUtil.AMBIANT_PRESSURE).maxPressureCapacity(2 * SteamUtil.AMBIANT_PRESSURE)
                .allowOvercharge(true).build();

        ROLLING_MILL = new CraftingMachineDescriptor.Builder("rollingmill")
                .recipe(QBarRecipeHandler.ROLLINGMILL_UID, 1f).inventory(3, 1, 1)
                .steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true).create();

        FURNACE_MK1 = new CraftingMachineDescriptor.Builder("furnacemk1").recipe(QBarRecipeHandler.FURNACE_UID, 1f)
                .inventory(3, 1, 1).steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true)
                .create();

        FURNACE_MK2 = new CraftingMachineDescriptor.Builder("furnacemk2").recipe(QBarRecipeHandler.FURNACE_UID, 1f)
                .inventory(3, 1, 1).steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true)
                .create();

        ASSEMBLER = new CraftingMachineDescriptor.Builder("assembler").recipe("", 1f).inventory(3, 1, 1)
                .steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true).create();

        ORE_WASHER = new CraftingMachineDescriptor.Builder("orewasher").recipe(QBarRecipeHandler.ORE_WASHER_UID, 1f)
                .inventory(4, 1, 2).inputTanks(new int[]{Fluid.BUCKET_VOLUME * 8})
                .steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true).create();

        SORTING_MACHINE = new CraftingMachineDescriptor.Builder("sortingmachine")
                .recipe(QBarRecipeHandler.SORTING_MACHINE_UID, 1f).inventory(6, 1, 4)
                .steam(2000, 10, SteamUtil.AMBIANT_PRESSURE, 1.5f * SteamUtil.AMBIANT_PRESSURE, true).create();
    }
}