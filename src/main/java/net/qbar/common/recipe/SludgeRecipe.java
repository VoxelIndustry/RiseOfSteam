package net.qbar.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.qbar.common.init.QBarItems;
import net.qbar.common.ore.QBarMineral;
import net.qbar.common.ore.SludgeData;

import javax.annotation.Nullable;
import java.util.Map;

public class SludgeRecipe implements IRecipe
{
    private ItemStack result = new ItemStack(QBarItems.COMPRESSED_MINERAL_SLUDGE);

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        for (int i = 0; i < 9; i++)
        {
            if (!inv.getStackInSlot(i).getItem().equals(QBarItems.MINERAL_SLUDGE))
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        SludgeData data = SludgeData.builder().build();

        for (int i = 0; i < 9; i++)
        {
            SludgeData previous = SludgeData
                    .fromNBT(inv.getStackInSlot(i).getTagCompound().getCompoundTag("sludgeData"));

            for (Map.Entry<QBarMineral, Float> ore : previous.getOres().entrySet())
                data.addOre(ore.getKey(), ore.getValue() / 9);
        }
        ItemStack result = this.result.copy();
        result.setTagCompound(new NBTTagCompound());
        result.getTagCompound().setTag("sludgeData", data.writeToNBT(new NBTTagCompound()));
        return result;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width == 3 && height == 3;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return this.result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name)
    {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName()
    {
        return null;
    }

    @Override
    public Class<IRecipe> getRegistryType()
    {
        return null;
    }
}
