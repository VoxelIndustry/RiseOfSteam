package net.ros.common.gui;

import net.ros.client.gui.*;

public class MachineGui
{
    public static final GuiReference BOILER = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSolidBoiler::new));

    public static final GuiReference KEYPUNCH = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiKeypunch::new));

    public static final GuiReference ROLLING_MILL = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiRollingMill::new));

    public static final GuiReference FLUID_TANK = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiFluidTank::new));

    public static final GuiReference STEAM_FURNACE = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSteamFurnace::new));

    public static final GuiReference STEAM_FURNACE_MK2 = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSteamFurnaceMK2::new));

    public static final GuiReference ASSEMBLER = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiAssembler::new));

    public static final GuiReference LIQUID_BOILER = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiLiquidBoiler::new));

    public static final GuiReference SOLAR_BOILER = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSolarBoiler::new));

    public static final GuiReference ORE_WASHER = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiOreWasher::new));

    public static final GuiReference SORTING_MACHINE = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSortingMachine::new));

    public static final GuiReference TINY_MINING_DRILL = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiTinyMiningDrill::new));

    public static final GuiReference SMALL_MINING_DRILL = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSmallMiningDrill::new));

    public static final GuiReference SAWMILL = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSawMill::new));

    public static final GuiReference ENGINEER_STORAGE = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiEngineerStorage::new));

    public static final GuiReference ENGINEER_WORKBENCH = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiEngineerWorkbench::new));

    public static final GuiReference BLUEPRINT_PRINTER = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiBlueprintPrinter::new));

    public static final GuiReference CRAFT_CARD_LIBRARY = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiCraftCardLibrary::new));

    public static final GuiReference ALLOY_CAULDRON = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiAlloyCauldron::new));

    public static final GuiReference CAPSULE_FILLER = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiCapsuleFiller::new));

    public static final GuiReference STEAM_TANK = new GuiReference(GuiManager::getContainer,
            GuiManager.getGuiContainer(GuiSteamTank::new));
}
