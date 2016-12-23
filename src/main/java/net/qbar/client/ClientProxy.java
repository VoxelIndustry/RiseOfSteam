package net.qbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.qbar.common.CommonProxy;
import net.qbar.common.init.QBarItems;
import net.qbar.common.items.ItemBase;

/**
 * @author Ourten 21 déc. 2016
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

		registerItemRender(QBarItems.itemPunchedCard);
    }

    private static void registerItemRender(final ItemBase item)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
