package net.qbar.common.steam;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemSteamTank extends SteamTank implements ISteamHandlerItem, ICapabilityProvider
{
    @Getter
    private ItemStack container;

    public ItemSteamTank(ItemStack container, int steamAmount, int capacity, float maxPressure)
    {
        super(steamAmount, capacity, maxPressure);

        this.container = container;
    }

    public ItemSteamTank(ItemStack container, int capacity, float maxPressure)
    {
        this(container, container.getTagCompound().getInteger("steam"), capacity, maxPressure);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilitySteamHandler.ITEM_STEAM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.ITEM_STEAM_HANDLER_CAPABILITY)
            return CapabilitySteamHandler.ITEM_STEAM_HANDLER_CAPABILITY.cast(this);
        return null;
    }

    @Override
    public int drainInternal(int amount, boolean doDrain)
    {
        int drained = super.drainInternal(amount, doDrain);

        if (doDrain)
        {
            this.getContainer().getTagCompound().setInteger("steam", this.getSteam());
            this.getContainer().getTagCompound().setFloat("pressure", this.getPressure());
        }
        return drained;
    }

    @Override
    public int fillInternal(int amount, boolean doFill)
    {
        int filled = super.fillInternal(amount, doFill);

        if (doFill)
        {
            this.getContainer().getTagCompound().setInteger("steam", this.getSteam());
            this.getContainer().getTagCompound().setFloat("pressure", this.getPressure());
        }
        return filled;
    }
}
