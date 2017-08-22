package net.qbar.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.qbar.client.gui.*;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.tile.machine.TileEngineerStorage;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.tile.machine.*;
import org.yggard.brokkgui.wrapper.BrokkGuiManager;

public class GuiHandler implements IGuiHandler
{
    public static final int BOILER_ID = 0;

    @Override
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
                                      final int y, final int z)
    {
        final EGui gui = EGui.values()[ID];
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

        final EGui gui = EGui.values()[ID];
        switch (gui)
        {
            case BOILER:
                return new GuiBoiler(player, (TileSolidBoiler) tile);
            case EXTRACTOR:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiExtractor(player, (TileExtractor) tile));
            case KEYPUNCH:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiKeypunch(player, (TileKeypunch) tile));
            case SPLITTER:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiSplitter(player, (TileSplitter) tile));
            case ROLLINGMILL:
                return new GuiRollingMill(player, (TileRollingMill) tile);
            case FLUIDTANK:
                return new GuiFluidTank(player, (TileTank) tile);
            case STEAMFURNACE:
                return new GuiSteamFurnace(player, (TileSteamFurnace) tile);
            case ASSEMBLER:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiAssembler(player, (TileAssembler) tile));
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
                return new GuiEngineerStorage(player, (TileEngineerStorage) tile);
            default:
                break;
        }
        return null;
    }
}
