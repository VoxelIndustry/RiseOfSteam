package net.qbar.common.card;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IPunchedCard
{
    IPunchedCard readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);

    void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced);

    boolean isValid(NBTTagCompound tag);

    int getID();
}
