package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.QBar;

import java.util.function.BiConsumer;

public interface IModelProvider
{
    @SideOnly(Side.CLIENT)
    int getItemModelCount();

    @SideOnly(Side.CLIENT)
    String getItemModelByIndex(int index);

    @SideOnly(Side.CLIENT)
    default BiConsumer<Integer, Block> registerItemModels()
    {
        return (i, block) -> ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(
                QBar.MODID + ":" + ((INamedBlock) block).getName(), this.getItemModelByIndex(i)));
    }
}
