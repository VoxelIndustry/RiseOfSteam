package net.qbar.common.machine.module.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.machine.component.FluidComponent;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ISerializableModule;
import net.qbar.common.machine.module.MachineModule;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FluidStorageModule extends MachineModule implements ISerializableModule
{
    private HashMap<String, IFluidHandler> fluidHandlers;

    public FluidStorageModule(IModularMachine machine)
    {
        super(machine, "FluidComponent");

        this.fluidHandlers = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromNBT(NBTTagCompound tag)
    {
        for (int i = 0; i < tag.getInteger("tankCount"); i++)
        {
            String name = tag.getString("tankName" + i);

            if (this.fluidHandlers.get(name) instanceof FluidTank)
                ((FluidTank) this.fluidHandlers.get(name)).readFromNBT(tag.getCompoundTag("tank" + i));
            else if (this.fluidHandlers.get(name) instanceof INBTSerializable)
                ((INBTSerializable) this.fluidHandlers.get(name)).deserializeNBT(tag.getTag("tank" + i));
        }
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        int i = 0;
        for (Map.Entry<String, IFluidHandler> entry : fluidHandlers.entrySet())
        {
            if (!(entry.getValue() instanceof FluidTank) && !(entry.getValue() instanceof INBTSerializable))
                continue;
            tag.setString("tankName" + i, entry.getKey());

            if (entry.getValue() instanceof FluidTank)
                tag.setTag("tank" + i, ((FluidTank) entry.getValue()).writeToNBT(new NBTTagCompound()));
            else if (entry.getValue() instanceof INBTSerializable)
                tag.setTag("tank" + i, ((INBTSerializable) entry.getValue()).serializeNBT());
            i++;
        }
        tag.setInteger("tankCount", i);
        return tag;
    }

    public IFluidHandler getFluidHandler(String name)
    {
        return this.fluidHandlers.get(name);
    }

    public void replaceFluidHandler(String name, IFluidHandler handler)
    {
        this.fluidHandlers.replace(name, handler);
    }

    public static Builder build(IModularMachine machine)
    {
        return new Builder(machine);
    }

    public static class Builder
    {
        private IModularMachine                machine;
        private HashMap<String, IFluidHandler> tanks;

        public Builder(IModularMachine machine)
        {
            this.tanks = new HashMap<>();
            this.machine = machine;
        }

        public Builder tank(String name, Function<FluidComponent, IFluidHandler> tankSupplier)
        {
            this.tanks.put(name, tankSupplier.apply(machine.getDescriptor().get(FluidComponent.class)));
            return this;
        }

        public FluidStorageModule create()
        {
            FluidStorageModule module = new FluidStorageModule(this.machine);
            module.fluidHandlers.putAll(tanks);
            return module;
        }
    }
}
