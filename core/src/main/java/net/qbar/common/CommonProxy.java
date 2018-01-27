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
import net.qbar.common.init.*;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.network.*;
import net.qbar.common.recipe.QBarMaterials;
import net.qbar.common.recipe.QBarRecipes;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.world.QBarOreGenerator;

public class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e)
    {
        QBarConstants.network = NetworkContext.forChannel(QBarConstants.MODID);
        QBarConstants.network.register(ContainerUpdatePacket.class);
        QBarConstants.network.register(TileSyncRequestPacket.class);
        QBarConstants.network.register(PipeUpdatePacket.class);
        QBarConstants.network.register(WrenchPacket.class);
        QBarConstants.network.register(MultiblockBoxPacket.class);
        QBarConstants.network.register(ServerActionHolderPacket.class);
        QBarConstants.network.register(ClientActionHolderPacket.class);
        QBarConstants.network.register(OpenGuiPacket.class);

        CapabilitySteamHandler.register();

        QBarMaterials.initMaterials();

        QBarBlocks.init();
        LogisticBlocks.init();
        MachineBlocks.init();
        WorldBlocks.init();

        QBarFluids.registerFluids();

        QBarItems.init();
        LogisticItems.init();
        WorldItems.init();
        MachineItems.init();

        MinecraftForge.EVENT_BUS.register(new QBarBlocks());
        MinecraftForge.EVENT_BUS.register(new QBarItems());
        MinecraftForge.EVENT_BUS.register(new QBarRecipes());

        MinecraftForge.EVENT_BUS.register(new TickHandler());
        CompatManager.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(QBar.instance, new GuiHandler());

        QBarMachines.preLoadMachines();
    }

    public void init(final FMLInitializationEvent e)
    {
        MinecraftForge.ORE_GEN_BUS.register(QBarOreGenerator.instance());
        MinecraftForge.EVENT_BUS.register(QBarOreGenerator.instance());
        GameRegistry.registerWorldGenerator(QBarOreGenerator.instance(), 0);

        CompatManager.init(e);

        QBarMachines.loadMachines();
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
