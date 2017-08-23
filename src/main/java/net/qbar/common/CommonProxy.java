package net.qbar.common;

import com.elytradev.concrete.network.NetworkContext;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.qbar.QBar;
import net.qbar.common.compat.CompatManager;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.GridManager;
import net.qbar.common.gui.GuiHandler;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.network.*;
import net.qbar.common.recipe.QBarMaterials;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.world.QBarOreGenerator;

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
        QBar.network.register(WrenchPacket.class);
        QBar.network.register(MultiblockBoxPacket.class);

        CapabilitySteamHandler.register();

        QBarMaterials.initMaterials();
        QBarBlocks.init();
        QBarFluids.registerFluids();
        QBarItems.init();

        MinecraftForge.EVENT_BUS.register(new QBarBlocks());
        MinecraftForge.EVENT_BUS.register(new QBarItems());
        MinecraftForge.EVENT_BUS.register(new QBarRecipeHandler());

        MinecraftForge.EVENT_BUS.register(new TickHandler());
        CompatManager.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(QBar.instance, new GuiHandler());

        QBarMachines.loadMachines();
    }

    public void init(final FMLInitializationEvent e)
    {
        MinecraftForge.ORE_GEN_BUS.register(QBarOreGenerator.instance());
        MinecraftForge.EVENT_BUS.register(QBarOreGenerator.instance());
        GameRegistry.registerWorldGenerator(QBarOreGenerator.instance(), 0);

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
