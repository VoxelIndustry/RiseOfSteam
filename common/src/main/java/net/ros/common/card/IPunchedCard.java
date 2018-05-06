package net.ros.common.card;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IPunchedCard
{
    IPunchedCard readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);

    void addInformation(ItemStack stack, List<String> tooltip, ITooltipFlag flag);

    boolean isValid(NBTTagCompound tag);

    int getID();
}
