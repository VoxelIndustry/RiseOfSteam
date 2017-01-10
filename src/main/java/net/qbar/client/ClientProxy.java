package net.qbar.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.qbar.QBar;
import net.qbar.common.CommonProxy;
import net.qbar.common.init.QBarItems;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

        this.registerItemRenderer(QBarItems.itemPunchedCard, 0, "punched_card");
    }

    @Override
    public void registerItemRenderer(final Item item, final int meta, final String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(QBar.MODID + ":" + id, "inventory"));
    }
}
