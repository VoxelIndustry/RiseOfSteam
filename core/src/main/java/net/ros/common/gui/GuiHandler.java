package net.ros.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.ros.client.gui.*;
import net.ros.common.ROSConstants;
import net.ros.common.container.IContainerProvider;
import net.ros.common.tile.TileStructure;
import net.ros.common.tile.machine.*;
import org.yggard.brokkgui.wrapper.impl.BrokkGuiManager;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
                                      final int y, final int z)
    {
        IGuiReference gui;

        if (ID < 100)
            gui = LogisticGui.values()[ID];
        else if (ID < 200)
            gui = MachineGui.values()[ID - 100];
        else
            gui = ResearchGui.values()[ID - 200];

        final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        if (gui.useContainerBuilder() && tile != null)
            return ((IContainerProvider) tile).createContainer(player);

        return null;
    }

    @Override
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
                                      final int y, final int z)
    {
        final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        if (tile instanceof TileStructure)
            return null;

        if (ID < 100)
        {
            LogisticGui gui = LogisticGui.values()[ID];

            switch (gui)
            {
                case EXTRACTOR:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiExtractor(player, (TileExtractor) tile));
                case SPLITTER:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiSplitter(player, (TileSplitter) tile));
                default:
                    return null;
            }
        }

        if(ID < 200) {
            final MachineGui gui = MachineGui.values()[ID - 100];
            switch (gui) {
                case BOILER:
                    return new GuiSolidBoiler(player, (TileSolidBoiler) tile);
                case KEYPUNCH:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiKeypunch(player, (TileKeypunch) tile));
                case ROLLINGMILL:
                    return new GuiRollingMill(player, (TileRollingMill) tile);
                case FLUIDTANK:
                    return new GuiFluidTank(player, (TileTank) tile);
                case STEAMFURNACE:
                    return new GuiSteamFurnace(player, (TileSteamFurnace) tile);
                case ASSEMBLER:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiAssembler(player, (TileAssembler) tile));
                case LIQUIDBOILER:
                    return new GuiLiquidBoiler(player, (TileLiquidBoiler) tile);
                case SOLARBOILER:
                    return new GuiSolarBoiler(player, (TileSolarBoiler) tile);
                case STEAMFURNACEMK2:
                    return new GuiSteamFurnaceMK2(player, (TileSteamFurnaceMK2) tile);
                case OREWASHER:
                    return new GuiOreWasher(player, (TileOreWasher) tile);
                case SORTINGMACHINE:
                    return new GuiSortingMachine(player, (TileSortingMachine) tile);
                case TINYMININGDRILL:
                    return new GuiTinyMiningDrill(player, (TileTinyMiningDrill) tile);
                case SMALLMININGDRILL:
                    return new GuiSmallMiningDrill(player, (TileSmallMiningDrill) tile);
                case SAWMILL:
                    return new GuiSawMill(player, (TileSawMill) tile);
                case ENGINEERSTORAGE:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiEngineerStorage(player, (TileEngineerStorage) tile));
                case BLUEPRINTPRINTER:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiBlueprintPrinter(player, (TileBlueprintPrinter) tile));
                case CRAFTCARDLIBRARY:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiCraftCardLibrary(player, (TileCraftCardLibrary) tile));
                case ALLOYCAULDRON:
                    return new GuiAlloyCauldron(player, (TileAlloyCauldron) tile);
                case ENGINEERWORKBENCH:
                    return BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                            new GuiEngineerWorkbench(player, (TileEngineerWorkbench) tile));
                case CAPSULE_FILLER:
                    return new GuiCapsuleFiller(player, (TileCapsuleFiller) tile);
                case STEAMTANK:
                    return new GuiSteamTank(player, (TileSteamTank) tile);
                default:
                    return null;
            }
        }

        ResearchGui gui = ResearchGui.values()[ID - 200];

        switch(gui)
        {
            case RESEARCH_BOOK:
                return BrokkGuiManager.getBrokkGuiScreen(new GuiBook());
        }
        return null;
    }
}
