package net.qbar.common.machine.module;

import lombok.Getter;
import net.qbar.common.tile.QBarTileBase;

@Getter
public abstract class MachineModule
{
    private IModularMachine machine;
    private String          name;

    public MachineModule(IModularMachine machine, String name)
    {
        this.machine = machine;
    }

    public QBarTileBase getMachineTile()
    {
        return (QBarTileBase) this.machine;
    }

    public boolean isClient()
    {
        return this.getMachineTile().isClient();
    }

    public boolean isServer()
    {
        return this.getMachineTile().isServer();
    }

    // TODO : Enhance the sync system with a per module sync control to limit network overhead
    public void sync()
    {
        this.getMachineTile().sync();
    }

    public void forceSync()
    {
        this.getMachineTile().forceSync();
    }
}
