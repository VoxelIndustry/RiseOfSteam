package net.qbar.common.machine.module;

import lombok.Getter;
import net.qbar.common.tile.QBarTileBase;

@Getter
public abstract class MachineModule
{
    private IModularMachine machine;
    private String name;

    public MachineModule(IModularMachine machine, String name)
    {
        this.machine = machine;
    }

    public QBarTileBase getMachineTile()
    {
        return (QBarTileBase) this.machine;
    }
}
