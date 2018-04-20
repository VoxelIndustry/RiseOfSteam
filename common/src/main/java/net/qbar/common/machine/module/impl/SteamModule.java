package net.qbar.common.machine.module.impl;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.grid.node.ISteamMachine;
import net.qbar.common.machine.component.SteamComponent;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ISerializableModule;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.steam.ISteamTank;

import java.util.function.Function;

public class SteamModule extends MachineModule implements ISerializableModule, ISteamMachine
{
    private final ISteamTank tank;

    @Getter
    @Setter
    private int grid;

    private final LinkedListMultimap<BlockPos, ISteamMachine> connectionsMap = LinkedListMultimap.create();

    public SteamModule(IModularMachine machine, Function<SteamComponent, ISteamTank> tankSupplier)
    {
        super(machine, "SteamModule");

        SteamComponent component = machine.getDescriptor().get(SteamComponent.class);
        this.tank = tankSupplier.apply(component);

        this.grid = -1;
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
