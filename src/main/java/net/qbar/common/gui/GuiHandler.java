package net.qbar.common.gui;

import org.yggard.brokkgui.wrapper.BrokkGuiManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.qbar.client.gui.GuiBoiler;
import net.qbar.client.gui.GuiExtractor;
import net.qbar.client.gui.GuiKeypunch;
import net.qbar.client.gui.GuiRollingMill;
import net.qbar.client.gui.GuiSplitter;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.tile.machine.TileBoiler;
import net.qbar.common.tile.machine.TileExtractor;
import net.qbar.common.tile.machine.TileKeypunch;
import net.qbar.common.tile.machine.TileRollingMill;
import net.qbar.common.tile.machine.TileSplitter;

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

        final EGui gui = EGui.values()[ID];
        switch (gui)
        {
            case BOILER:
                return new GuiBoiler(player, (TileBoiler) tile);
            case EXTRACTOR:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiExtractor(player, (TileExtractor) tile));
            case KEYPUNCH:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiKeypunch(player, (TileKeypunch) tile));
            case SPLITTER:
                return BrokkGuiManager.getBrokkGuiContainer(new GuiSplitter(player, (TileSplitter) tile));
            case ROLLINGMILL:
                return new GuiRollingMill(player, (TileRollingMill) tile);
            default:
                break;
        }

        return null;
    }

}
