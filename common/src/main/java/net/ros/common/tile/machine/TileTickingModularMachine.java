package net.ros.common.tile.machine;

import net.minecraft.util.ITickable;
import net.ros.common.machine.MachineDescriptor;
import net.ros.common.machine.module.ITickableModule;
import net.ros.common.machine.module.MachineModule;

import java.util.ArrayList;
import java.util.List;

public class TileTickingModularMachine extends TileModularMachine implements ITickable
{
    private List<ITickableModule> tickings;

    public TileTickingModularMachine(MachineDescriptor descriptor)
    {
        super(descriptor);
    }

    public TileTickingModularMachine()
    {
        this(null);
    }

    @Override
    public void update()
    {
        if (this.getDescriptor() != null)
        {
            this.syncLock();
            this.getTickings().forEach(ITickableModule::tick);
            this.releaseSyncLock(true);
        }
    }

    @Override
    protected void addModule(MachineModule module)
    {
        super.addModule(module);

        if (module instanceof ITickableModule)
            this.getTickings().add((ITickableModule) module);
    }

    @Override
    protected void removeModule(MachineModule module)
    {
        super.removeModule(module);

        if (module instanceof ITickableModule)
            this.getTickings().remove(module);
    }

    @Override
    protected void reloadModules()
    {
        this.getTickings().clear();
        super.reloadModules();
    }

    private List<ITickableModule> getTickings()
    {
        if (this.tickings == null)
            this.tickings = new ArrayList<>();
        return this.tickings;
    }
}
