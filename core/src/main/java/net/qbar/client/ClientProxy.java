package net.qbar.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.qbar.QBar;
import net.qbar.client.render.BlueprintRender;
import net.qbar.client.render.model.obj.QBarOBJLoader;
import net.qbar.client.render.tile.RenderBelt;
import net.qbar.client.render.tile.RenderRollingMill;
import net.qbar.client.render.tile.RenderSteamFurnaceMK2;
import net.qbar.client.render.tile.RenderStructure;
import net.qbar.common.CommonProxy;
import net.qbar.common.QBarConstants;
import net.qbar.common.block.IModelProvider;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;
import net.qbar.common.item.IItemModelProvider;
import net.qbar.common.network.MultiblockBoxPacket;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.tile.machine.TileBelt;
import net.qbar.common.tile.machine.TileRollingMill;
import net.qbar.common.tile.machine.TileSteamFurnaceMK2;
import org.lwjgl.input.Mouse;

import java.util.function.BiConsumer;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(final FMLPreInitializationEvent e)
    {
        OBJLoader.INSTANCE.addDomain(QBarConstants.MODID);
        ModelLoaderRegistry.registerLoader(QBarOBJLoader.INSTANCE);
        QBarOBJLoader.INSTANCE.addDomain(QBarConstants.MODID);

        MinecraftForge.EVENT_BUS.register(this);
        super.preInit(e);

        QBarOBJLoader.INSTANCE.addRetexturedModel("_belt_animated.mwm",
                new ResourceLocation(QBarConstants.MODID + ":block/belt.mwm"), new String[]{"Top"},
                new String[]{"qbar:blocks/belt_top_anim"});
        QBarOBJLoader.INSTANCE.addRetexturedModel("_belt_slope_animated.mwm",
                new ResourceLocation(QBarConstants.MODID + ":block/belt_slope.mwm"), new String[]{"None"},
                new String[]{"qbar:blocks/belt_slope_anim"});
        QBarOBJLoader.INSTANCE.addRetexturedModel("_belt_slope2_animated.mwm",
                new ResourceLocation(QBarConstants.MODID + ":block/belt_slope2.mwm"), new String[]{"None"},
                new String[]{"qbar:blocks/belt_slope_anim"});

        ClientProxy.registerFluidsClient();
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());

        QBarItems.ITEMS.stream().filter(item -> item instanceof IItemModelProvider)
                .forEach(item -> ((IItemModelProvider) item).registerVariants());
    }

    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

        ClientRegistry.bindTileEntitySpecialRenderer(TileBelt.class, new RenderBelt());
        ClientRegistry.bindTileEntitySpecialRenderer(TileStructure.class, new RenderStructure());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRollingMill.class, new RenderRollingMill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSteamFurnaceMK2.class, new RenderSteamFurnaceMK2());
        MinecraftForge.EVENT_BUS.register(new ClientEventManager());
    }

    @Override
    public void registerItemRenderer(final Item item, final int meta, final String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(QBarConstants.MODID + ":" + id, "inventory"));
    }

    public static final void registerFluidsClient()
    {
        final ModelResourceLocation fluidSteamLocation = new ModelResourceLocation(QBarConstants.MODID + ":" +
                "blockfluidsteam",
                "steam");
        ModelLoader.setCustomStateMapper(QBarFluids.blockFluidSteam, new StateMapperBase()
        {
            @Override
            protected ModelResourceLocation getModelResourceLocation(final IBlockState state)
            {
                return fluidSteamLocation;
            }
        });

        ModelBakery.registerItemVariants(Item.getItemFromBlock(QBarFluids.blockFluidSteam), fluidSteamLocation);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(QBarFluids.blockFluidSteam),
                stack -> fluidSteamLocation);
    }

    @SubscribeEvent
    public void onModelBake(final ModelBakeEvent evt)
    {
        final ModelResourceLocation key = new ModelResourceLocation(QBarConstants.MODID + ":" + QBarItems.BLUEPRINT
                .name,
                "inventory");
        final IBakedModel originalModel = evt.getModelRegistry().getObject(key);
        evt.getModelRegistry().putObject(key, new BlueprintRender(originalModel));

        for (Item item : QBarItems.ITEMS)
        {
            if (item instanceof IItemModelProvider && ((IItemModelProvider) item).hasSpecialModel())
                ((IItemModelProvider) item).registerModels();
            else
                QBar.proxy.registerItemRenderer(item, 0, item.getUnlocalizedName().substring(5));
        }

        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:fluidpipe"), 1,
                new ModelResourceLocation(QBarConstants.MODID + ":fluidpipe", "inventoryvalve"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:steampipe"), 1,
                new ModelResourceLocation(QBarConstants.MODID + ":steampipe", "inventoryvalve"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:itemextractor"), 1,
                new ModelResourceLocation(QBarConstants.MODID + ":itemextractor", "facing=down,filter=true"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:itemsplitter"), 1,
                new ModelResourceLocation(QBarConstants.MODID + ":itemsplitter", "facing=up,filter=true"));

        QBarBlocks.BLOCKS.keySet().stream().filter(IModelProvider.class::isInstance).forEach(block ->
        {
            IModelProvider modelProvider = (IModelProvider) block;

            BiConsumer<Integer, Block> modelRegister = modelProvider.registerItemModels();
            for (int i = 0; i < modelProvider.getItemModelCount(); i++)
                modelRegister.accept(i, block);
        });
    }

    @SubscribeEvent
    public void onRightClick(GuiScreenEvent.MouseInputEvent event)
    {
        if (event.getGui() instanceof GuiContainer && Mouse.isButtonDown(1))
        {
            GuiContainer container = (GuiContainer) event.getGui();
            if (container.getSlotUnderMouse() != null
                    && Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty()
                    && container.getSlotUnderMouse().getStack().getItem() == QBarItems.MULTIBLOCK_BOX)
            {
                new MultiblockBoxPacket(container.getSlotUnderMouse().slotNumber).sendToServer();
                event.setCanceled(true);
            }
        }
    }
}
