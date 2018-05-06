package net.ros.common.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.ros.common.init.ROSItems;
import net.ros.common.steam.SteamCapabilities;

public class CapsuleRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private ItemStack result = new ItemStack(ROSItems.STEAMCAPSULE);

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        int capsuleCount = 0;
        int items = 0;

        for (int i = 0; i < 9; i++)
        {
            if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE))
            {
                capsuleCount++;
                items++;
            }
            else if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE_X4))
            {
                capsuleCount += 4;
                items++;
            }
            else if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE_X6))
            {
                capsuleCount += 6;
                items++;
            }
            else if (!inv.getStackInSlot(i).isEmpty())
                return false;
        }

        return items >= 2 && (capsuleCount == 4 || capsuleCount == 6 || capsuleCount == 12);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int capsuleCount = 0;
        int steam = 0;

        for (int i = 0; i < 9; i++)
        {
            if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE))
                capsuleCount++;
            else if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE_X4))
                capsuleCount += 4;
            else if (inv.getStackInSlot(i).getItem().equals(ROSItems.STEAMCAPSULE_X6))
                capsuleCount += 6;

            if (inv.getStackInSlot(i).hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER,
                    EnumFacing.NORTH))
                steam += inv.getStackInSlot(i).getCapability(SteamCapabilities.ITEM_STEAM_HANDLER,
                        EnumFacing.NORTH).getSteam();
        }

        Item resultItem = ROSItems.STEAMCAPSULE;
        if (capsuleCount == 4)
            resultItem = ROSItems.STEAMCAPSULE_X4;
        else if (capsuleCount == 6)
            resultItem = ROSItems.STEAMCAPSULE_X6;
        else if (capsuleCount == 12)
            resultItem = ROSItems.STEAMCAPSULE_X12;

        ItemStack result = new ItemStack(resultItem);
        if (result.hasTagCompound())
            result.setTagCompound(new NBTTagCompound());
        result.getCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH)
                .fillSteam(steam, true);

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