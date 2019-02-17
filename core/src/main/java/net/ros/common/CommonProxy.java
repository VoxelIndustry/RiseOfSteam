package net.ros.common;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.ros.RiseOfSteam;
import net.ros.common.compat.CompatManager;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.GridManager;
import net.ros.common.gui.GuiHandler;
import net.ros.common.heat.HeatCapabilities;
import net.ros.common.init.*;
import net.ros.common.machine.Machines;
import net.ros.common.network.*;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Recipes;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.world.OreGenerator;
import net.voxelindustry.steamlayer.network.packet.PacketHandler;

public class CommonProxy
{
    public void preInit(final FMLPreInitializationEvent e)
    {
        PacketHandler.getInstance().register(PipeUpdatePacket.class);
        PacketHandler.getInstance().register(WrenchPacket.class);
        PacketHandler.getInstance().register(MultiblockBoxPacket.class);
        PacketHandler.getInstance().register(OpenGuiPacket.class);
        PacketHandler.getInstance().register(SteamEffectPacket.class);

        SteamCapabilities.register();
        HeatCapabilities.register();

        Materials.initMaterials();

        ROSBlocks.init();
        LogisticBlocks.init();
        MachineBlocks.init();
        WorldBlocks.init();

        ROSFluids.registerFluids();

        ROSItems.init();
        LogisticItems.init();
        WorldItems.init();
        MachineItems.init();

        MinecraftForge.EVENT_BUS.register(new ROSBlocks());
        MinecraftForge.EVENT_BUS.register(new ROSItems());
        MinecraftForge.EVENT_BUS.register(new Recipes());

        MinecraftForge.EVENT_BUS.register(new TickHandler());
        CompatManager.preInit(e);

        NetworkRegistry.INSTANCE.registerGuiHandler(RiseOfSteam.instance, new GuiHandler());

        Machines.preLoadMachines();
    }

    public void init(final FMLInitializationEvent e)
    {
        MinecraftForge.ORE_GEN_BUS.register(OreGenerator.instance());
        MinecraftForge.EVENT_BUS.register(OreGenerator.instance());
        GameRegistry.registerWorldGenerator(OreGenerator.instance(), 0);

        CompatManager.init(e);

        Machines.loadMachines();
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

    public void registerItemRenderer(final Item item, final int meta)
    {

    }
}
