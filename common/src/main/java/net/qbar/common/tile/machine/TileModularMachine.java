package net.qbar.common.tile.machine;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.module.CapabilityModule;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ISerializableModule;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.tile.QBarTileBase;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class TileModularMachine extends QBarTileBase implements IModularMachine
{
    private HashMap<Class<? extends MachineModule>, MachineModule> modules;
    @Getter
    private MachineDescriptor                                      descriptor;

    public TileModularMachine(MachineDescriptor descriptor)
    {
        this.descriptor = descriptor;

        this.modules = new HashMap<>();
        if (descriptor != null)
            this.reloadModules();
    }

    public TileModularMachine()
    {
        this(null);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        MachineDescriptor previous = this.descriptor;
        this.descriptor = QBarMachines.get(tag.getString("machineDescriptor"));

        if (previous == null && this.descriptor != null)
            this.reloadModules();

        modules.values().forEach(module ->
        {
            if (module instanceof ISerializableModule)
                ((ISerializableModule) module).fromNBT(tag.getCompoundTag(module.getName()));
        });
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setString("machineDescriptor", this.descriptor.getName());

        modules.values().forEach(module ->
        {
            if (module instanceof ISerializableModule)
                tag.setTag(module.getName(), ((ISerializableModule) module).toNBT(new NBTTagCompound()));
        });
        return super.writeToNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (this.hasModule(CapabilityModule.class) && this.getModule(CapabilityModule.class)
                .hasCapability(capability, BlockPos.ORIGIN, facing)) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (this.hasModule(CapabilityModule.class))
        {
            T result = this.getModule(CapabilityModule.class).getCapability(capability, BlockPos.ORIGIN, facing);
            if (result != null)
                return result;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public Collection<MachineModule> getModules()
    {
        return this.modules.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends MachineModule> T getModule(Class<T> moduleClass)
    {
        return (T) this.modules.get(moduleClass);
    }

    @Override
    public <T extends MachineModule> boolean hasModule(Class<T> moduleClass)
    {
        return this.modules.containsKey(moduleClass);
    }

    protected void addModule(MachineModule module)
    {
        this.modules.put(module.getClass(), module);
    }

    protected void removeModule(MachineModule module)
    {
        this.modules.remove(module.getClass());
    }

    protected void reloadModules()
    {
        this.modules.clear();
    }
}
