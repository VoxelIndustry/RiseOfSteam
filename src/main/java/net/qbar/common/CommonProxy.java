package net.qbar.common;

import com.elytradev.concrete.NetworkContext;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.qbar.QBar;
import net.qbar.common.compat.CompatManager;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.GridManager;
import net.qbar.common.gui.GuiHandler;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;
import net.qbar.common.network.*;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;

public class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e)
    {
        QBar.network = NetworkContext.forChannel(QBar.MODID);
        QBar.network.register(ContainerUpdatePacket.class);
        QBar.network.register(TileSyncRequestPacket.class);
        QBar.network.register(KeypunchPacket.class);
        QBar.network.register(FilteredMachinePacket.class);
        QBar.network.register(PipeUpdatePacket.class);

        CapabilitySteamHandler.register();

        QBarBlocks.registerBlocks();
        QBarItems.registerItems();
        QBarFluids.registerFluids();

        MinecraftForge.EVENT_BUS.register(new TickHandler());
        CompatManager.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(QBar.instance, new GuiHandler());
    }

    public void init(final FMLInitializationEvent e)
    {
        QBarRecipeHandler.registerRecipes();

        CompatManager.init(e);
    }

    public void postInit(final FMLPostInitializationEvent e)
    {
        CompatManager.postInit(e);
    }

    public void serverStarted(final FMLServerStartedEvent e)
    {
    }

    public void serverStopping(final FMLServerStoppingEvent e)
    {
        GridManager.getInstance().cableGrids.clear();
    }

    public void registerItemRenderer(final Item item, final int meta, final String id)
    {

    }
}
