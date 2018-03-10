package net.qbar.common.tile.machine;

import net.minecraft.util.ITickable;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.machine.module.ITickableModule;
import net.qbar.common.machine.module.MachineModule;

import java.util.ArrayList;
import java.util.List;

public class TileTickingModularMachine extends TileModularMachine implements ITickable
{
    private List<ITickableModule> tickables = new ArrayList<>();

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
        this.tickables.forEach(ITickableModule::tick);
    }

    @Override
    protected void addModule(MachineModule module)
    {
        super.addModule(module);

        if (module instanceof ITickableModule)
            this.tickables.add((ITickableModule) module);
    }

    @Override
    protected void removeModule(MachineModule module)
    {
        super.removeModule(module);

        if (module instanceof ITickableModule)
            this.tickables.remove(module);
    }

    @Override
    protected void reloadModules()
    {
        this.tickables.clear();
        super.reloadModules();
    }
}
