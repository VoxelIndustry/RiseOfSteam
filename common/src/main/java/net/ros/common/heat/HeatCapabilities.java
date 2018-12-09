package net.ros.common.heat;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class HeatCapabilities
{
    @CapabilityInject(IHeatHandler.class)
    public static Capability<IHeatHandler> HEAT_HANDLER = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultHeatHandlerStorage<>(),
                () -> new HeatTank(1000));
    }

    private static class DefaultHeatHandlerStorage<T extends IHeatHandler> implements Capability.IStorage<T>
    {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
        {
            if (!(instance instanceof IHeatTank))
                throw new RuntimeException("IHeatHandler instance is not instance of HeatTank");

            return ((HeatTank) instance).writeToNBT(new NBTTagCompound());
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt)
        {
            if (!(instance instanceof IHeatTank))
                throw new RuntimeException("IHeatHandler instance is not instance of HeatTank");

            ((HeatTank) instance).readFromNBT(new NBTTagCompound());
        }
    }
}
