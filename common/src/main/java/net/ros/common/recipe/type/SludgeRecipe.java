package net.ros.common.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.ros.common.init.ROSItems;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.SludgeData;

import java.util.Map;

public class SludgeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private ItemStack result = new ItemStack(ROSItems.COMPRESSED_MINERAL_SLUDGE);

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        for (int i = 0; i < 9; i++)
        {
            if (!inv.getStackInSlot(i).getItem().equals(ROSItems.MINERAL_SLUDGE))
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        SludgeData data = new SludgeData();

        for (int i = 0; i < 9; i++)
        {
            SludgeData previous = SludgeData
                    .fromNBT(inv.getStackInSlot(i).getTagCompound().getCompoundTag("sludgeData"));

            for (Map.Entry<Mineral, Float> ore : previous.getOres().entrySet())
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
}
