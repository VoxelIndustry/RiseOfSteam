package net.qbar.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.qbar.QBar;
import net.qbar.client.render.tile.RenderBelt;
import net.qbar.common.CommonProxy;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.TileBelt;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(final FMLPreInitializationEvent e)
    {
        OBJLoader.INSTANCE.addDomain(QBar.MODID);

        super.preInit(e);
    }

    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

        this.registerItemRenderer(QBarItems.PUNCHED_CARD, 0, "punched_card");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(QBarBlocks.FLUID_TANK), 0,
                new ModelResourceLocation(QBar.MODID + ":fluidtank", "inventory"));

        ClientRegistry.bindTileEntitySpecialRenderer(TileBelt.class, new RenderBelt());
    }

    @Override
    public void registerItemRenderer(final Item item, final int meta, final String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(QBar.MODID + ":" + id, "inventory"));
    }
}
