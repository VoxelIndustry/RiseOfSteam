package net.qbar.common.steam;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilitySteamHandler
{
    @CapabilityInject(ISteamHandler.class)
    public static Capability<ISteamHandler> STEAM_HANDLER_CAPABILITY = null;

    @CapabilityInject(ISteamHandlerItem.class)
    public static Capability<ISteamHandlerItem> ITEM_STEAM_HANDLER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISteamHandler.class, new DefaultSteamHandlerStorage<>(),
                () -> new SteamTank(0, 10000, SteamUtil.BASE_PRESSURE * 2));

        CapabilityManager.INSTANCE.register(ISteamHandlerItem.class, new DefaultSteamHandlerStorage<>(),
                () -> new ItemSteamTank(ItemStack.EMPTY, 0, 10000, SteamUtil.BASE_PRESSURE * 2));
    }

    private static class DefaultSteamHandlerStorage<T extends ISteamHandler> implements Capability.IStorage<T>
    {
        @Override
        public NBTBase writeNBT(final Capability<T> capability, final T instance, final EnumFacing side)
        {
            if (!(instance instanceof ISteamTank))
                throw new RuntimeException("ISteamHandler instance is not instance of SteamTank");
            final NBTTagCompound nbt = new NBTTagCompound();

            ((SteamTank) instance).writeToNBT(nbt);
            return nbt;
        }

        @Override
        public void readNBT(final Capability<T> capability, final T instance, final EnumFacing side, final NBTBase nbt)
        {
            if (!(instance instanceof ISteamTank))
                throw new RuntimeException("ISteamHandler instance is not instance of SteamTank");
            final NBTTagCompound tags = (NBTTagCompound) nbt;
            final SteamTank tank = (SteamTank) instance;
            tank.readFromNBT(tags);
        }
    }
}
