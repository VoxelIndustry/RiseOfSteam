package net.qbar.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.qbar.common.recipe.QBarRecipeHandler;

public class ItemPlate extends ItemBase
{
    public ItemPlate()
    {
        super("metalplate");
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        if (tab == this.getCreativeTab())
        {
            QBarRecipeHandler.metals.forEach(metal ->
            {
                final ItemStack stack = new ItemStack(this);
                final NBTTagCompound tag = new NBTTagCompound();
                stack.setTagCompound(tag);

                tag.setString("metal", metal);
                list.add(stack);
            });
        }
    }

    @Override
    public String getUnlocalizedName(final ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("metal"))
            return this.getUnlocalizedName() + "." + stack.getTagCompound().getString("metal");
        return this.getUnlocalizedName();
    }
}
