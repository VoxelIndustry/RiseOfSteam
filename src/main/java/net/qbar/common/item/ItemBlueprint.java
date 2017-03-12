package net.qbar.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.qbar.common.multiblock.Blueprints;

public class ItemBlueprint extends ItemBase
{
    public ItemBlueprint()
    {
        super("blueprint");
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(final Item item, final CreativeTabs tab, final NonNullList<ItemStack> list)
    {
        Blueprints.getInstance().getBlueprints().forEach((name, blueprint) ->
        {
            final ItemStack stack = new ItemStack(this);
            final NBTTagCompound tag = new NBTTagCompound();
            stack.setTagCompound(tag);

            tag.setString("blueprint", name);
            list.add(stack);
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getItemStackDisplayName(final ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("blueprint"))
            return I18n
                    .translateToLocalFormatted("item.blueprint.name", new Object[] {
                            I18n.translateToLocal("tile." + stack.getTagCompound().getString("blueprint") + ".name") })
                    .trim();
        return this.name;
    }
}
