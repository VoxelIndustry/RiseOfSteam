package net.qbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.qbar.common.CommonProxy;
import net.qbar.common.init.QBarItems;

/**
 * @author Ourten 21 déc. 2016
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void init(final FMLInitializationEvent e)
    {
        super.init(e);

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(QBarItems.itemPunchedCard, 0,
                new ModelResourceLocation(QBarItems.itemPunchedCard.getRegistryName(), "inventory"));
    }
}
