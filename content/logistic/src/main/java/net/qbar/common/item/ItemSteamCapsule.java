package net.qbar.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.steam.ItemSteamTank;
import net.qbar.common.steam.SteamUtil;

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
        this.setMaxStackSize(1);

        this.capacity = capacity;
        this.pressure = pressure;

        this.addPropertyOverride(new ResourceLocation("energy"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
            {
                return stack.getTagCompound().getInteger("steam") / (capacity * pressure);
            }
        });
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        if (stack.hasTagCompound())
            return 1 - (stack.getTagCompound().getInteger("steam") / (capacity * pressure));
        return 0;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
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
