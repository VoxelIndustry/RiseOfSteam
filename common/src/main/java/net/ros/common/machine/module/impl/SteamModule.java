package net.ros.common.machine.module.impl;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.grid.node.ISteamMachine;
import net.ros.common.machine.module.IModularMachine;
import net.ros.common.machine.SteamOverloadManager;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.ISerializableModule;
import net.ros.common.machine.module.MachineModule;
import net.ros.common.steam.ISteamHandler;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.ListenerSteamTank;

import java.util.function.Function;

public class SteamModule extends MachineModule implements ISerializableModule, ISteamMachine
{
    private final ListenerSteamTank tank;

    @Getter
    @Setter
    private int grid;

    private final LinkedListMultimap<BlockPos, ISteamMachine> connectionsMap = LinkedListMultimap.create();

    public SteamModule(IModularMachine machine, Function<SteamComponent, ISteamTank> tankSupplier)
    {
        super(machine, "SteamModule");

        SteamComponent component = machine.getDescriptor().get(SteamComponent.class);
        this.tank = new ListenerSteamTank(tankSupplier.apply(component));

        this.tank.setOnSteamChange(this::refreshSteam);

        this.grid = -1;
    }

    private void refreshSteam()
    {
        if(this.isClient())
            return;

        if (this.getInternalSteamHandler().getPressure() >= this.getInternalSteamHandler().getSafePressure())
        {
            if (!SteamOverloadManager.getInstance().hasMachine(this.getMachineTile()))
                SteamOverloadManager.getInstance().addMachine(this.getMachineTile());
        }
        else if (SteamOverloadManager.getInstance().hasMachine(this.getMachineTile()))
            SteamOverloadManager.getInstance().removeMachine(this.getMachineTile());
    }

    public ISteamHandler getSteamHandler()
    {
        return this.hasGrid() ? this.getGridObject().getMesh() : tank;
    }

    @Override
    public ISteamTank getInternalSteamHandler()
    {
        return this.tank;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.tank.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        return this.tank.writeToNBT(tag);
    }

    @Override
    public LinkedListMultimap<BlockPos, ISteamMachine> getConnectionsMap()
    {
        return this.connectionsMap;
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getMachineTile().getPos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.getMachineTile().getWorld();
    }
}
