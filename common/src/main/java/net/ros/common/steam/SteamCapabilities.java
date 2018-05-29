package net.ros.common.steam;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.ros.common.grid.node.ISteamMachine;
import net.ros.common.grid.node.SteamMachine;
import net.ros.common.machine.module.impl.SteamModule;

import javax.annotation.Nullable;

public class SteamCapabilities
{
    @CapabilityInject(ISteamHandler.class)
    public static Capability<ISteamHandler> STEAM_HANDLER = null;

    @CapabilityInject(ISteamHandlerItem.class)
    public static Capability<ISteamHandlerItem> ITEM_STEAM_HANDLER = null;

    @CapabilityInject(ISteamMachine.class)
    public static Capability<ISteamMachine> STEAM_MACHINE = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISteamHandler.class, new DefaultSteamHandlerStorage<>(),
                () -> new SteamTank(10000, SteamUtil.BASE_PRESSURE * 2));

        CapabilityManager.INSTANCE.register(ISteamHandlerItem.class, new DefaultSteamHandlerStorage<>(),
                () -> new ItemSteamTank(ItemStack.EMPTY, 10000, SteamUtil.BASE_PRESSURE * 2));

        CapabilityManager.INSTANCE.register(ISteamMachine.class, new DefaultSteamMachineStorage<>(),
                () -> new SteamMachine(BlockPos.ORIGIN, null));
    }

    private static class DefaultSteamHandlerStorage<T extends ISteamHandler> implements Capability.IStorage<T>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(final Capability<T> capability, final T instance, final EnumFacing side)
        {
            if (!(instance instanceof ISteamTank))
                throw new RuntimeException("ISteamHandler instance is not instance of SteamTank");

            return ((SteamTank) instance).writeToNBT(new NBTTagCompound());
        }

        @Override
        public void readNBT(final Capability<T> capability, final T instance, final EnumFacing side, final NBTBase nbt)
        {
            if (!(instance instanceof ISteamTank))
                throw new RuntimeException("ISteamHandler instance is not instance of SteamTank");

            ((SteamTank) instance).readFromNBT(new NBTTagCompound());
        }
    }

    private static class DefaultSteamMachineStorage<T extends ISteamMachine> implements Capability.IStorage<T>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
        {
            if (!(instance instanceof SteamModule))
                throw new RuntimeException("ISteamMachine instance is not instance of SteamModule");

            return ((SteamModule) instance).toNBT(new NBTTagCompound());
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt)
        {
            if (!(instance instanceof SteamModule))
                throw new RuntimeException("ISteamMachine instance is not instance of SteamModule");

            ((SteamModule) instance).fromNBT((NBTTagCompound) nbt);
        }
    }
}
