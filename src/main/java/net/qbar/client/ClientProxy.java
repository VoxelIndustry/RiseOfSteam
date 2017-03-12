package net.qbar.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.qbar.QBar;
import net.qbar.client.render.BlueprintRender;
import net.qbar.client.render.tile.RenderBelt;
import net.qbar.common.CommonProxy;
import net.qbar.common.init.QBarFluids;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.machine.TileBelt;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(final FMLPreInitializationEvent e)
    {
        OBJLoader.INSTANCE.addDomain(QBar.MODID);
        MinecraftForge.EVENT_BUS.register(this);

        super.preInit(e);

        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:fluidpipe"), 1,
                new ModelResourceLocation(QBar.MODID + ":fluidpipe", "inventoryvalve"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:steampipe"), 1,
                new ModelResourceLocation(QBar.MODID + ":steampipe", "inventoryvalve"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:itemextractor"), 1,
                new ModelResourceLocation(QBar.MODID + ":itemextractor", "facing=down,filter=true"));
        ModelLoader.setCustomModelResourceLocation(Item.getByNameOrId("qbar:itemsplitter"), 1,
                new ModelResourceLocation(QBar.MODID + ":itemsplitter", "facing=up,filter=true"));

        this.registerItemRenderer(Item.getByNameOrId("qbar:punched_card"), 1, "punched_card1");
        this.registerItemRenderer(Item.getByNameOrId("qbar:blueprint"), 0, "blueprint");

        ClientProxy.registerFluidsClient();
    }

    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

        ClientRegistry.bindTileEntitySpecialRenderer(TileBelt.class, new RenderBelt());
    }

    @Override
    public void registerItemRenderer(final Item item, final int meta, final String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(QBar.MODID + ":" + id, "inventory"));
    }

    public static final void registerFluidsClient()
    {
        final ModelResourceLocation fluidSteamLocation = new ModelResourceLocation(QBar.MODID + ":" + "blockfluid",
                "steam");
        ModelLoader.setCustomStateMapper(QBarFluids.blockFluidSteam, new StateMapperBase()
        {
            @Override
            protected ModelResourceLocation getModelResourceLocation(final IBlockState state)
            {
                return fluidSteamLocation;
            }
        });

        ModelBakery.registerItemVariants(Item.getItemFromBlock(QBarFluids.blockFluidSteam));
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(QBarFluids.blockFluidSteam),
                stack -> fluidSteamLocation);
    }

    @SubscribeEvent
    public void onModelBake(final ModelBakeEvent evt)
    {
        System.out.println("CAAAAAAALLED BANANA");
        final ModelResourceLocation key = new ModelResourceLocation(QBar.MODID + ":" + QBarItems.BLUEPRINT.name,
                "inventory");
        final IBakedModel originalModel = evt.getModelRegistry().getObject(key);
        evt.getModelRegistry().putObject(key, new BlueprintRender(originalModel));

    }
}
