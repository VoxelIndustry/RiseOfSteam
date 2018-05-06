package net.ros.common.machine.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ros.common.tile.TileBase;

@Getter
@AllArgsConstructor
public abstract class MachineModule
{
    private IModularMachine machine;
    private String          name;

    public <T extends TileBase & IModularMachine> T getMachineTile()
    {
        return (T) this.machine;
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
