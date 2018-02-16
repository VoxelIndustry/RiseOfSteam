package net.qbar.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.steam.*;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSteamCapsule extends ItemBase
{
    private final int   capacity;
    private final float pressure;

    public ItemSteamCapsule(String name, int capacity, float pressure)
    {
        super(name);
        this.setHasSubtypes(true);

        this.capacity = capacity;
        this.pressure = pressure;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.getTagCompound() == null)
            return;
        tooltip.add("Steam: " + stack.getTagCompound().getInteger("steam") + " / " + this.capacity);
        tooltip.add("Pressure: " + SteamUtil.pressureFormat.format(stack.getTagCompound().getFloat("pressure"))
                + " /" + " " + SteamUtil.pressureFormat.format(this.pressure));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        return new ItemSteamTank(stack, capacity, pressure);
    }
}
