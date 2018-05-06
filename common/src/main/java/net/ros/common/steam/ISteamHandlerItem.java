package net.ros.common.steam;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface ISteamHandlerItem extends ISteamHandler
{
    @Nonnull
    ItemStack getContainer();
}
