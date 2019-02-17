package net.ros.common.tile.machine;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.machine.MachineDescriptor;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.*;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.multiblock.ITileMultiblockCore;
import net.voxelindustry.hermod.EventDispatcher;
import net.voxelindustry.steamlayer.tile.ITileInfoList;
import net.voxelindustry.steamlayer.tile.TileBase;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class TileModularMachine extends TileBase implements IModularMachine, ITileMultiblockCore
{
    private HashMap<Class<? extends MachineModule>, MachineModule> modules;

    @Getter
    private MachineDescriptor descriptor;

    private EventDispatcher eventDispatcher;

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
    public void addInfo(ITileInfoList list)
    {
        super.addInfo(list);

        if (this.getDescriptor() != null)
        {
            this.getModules().forEach(module ->
            {
                if (module instanceof IInfoModule)
                    ((IInfoModule) module).addInfo(list);
            });
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        MachineDescriptor previous = this.descriptor;
        this.descriptor = Machines.get(tag.getString("machineDescriptor"));

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
        if (this.descriptor != null)
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
        return this.hasCapability(capability, BlockPos.ORIGIN, facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        T result = this.getCapability(capability, BlockPos.ORIGIN, facing);

        if (result != null)
            return result;
        return super.getCapability(capability, facing);
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);

        if(this.hasModule(InventoryModule.class))
            this.getModule(InventoryModule.class).dropAll(this.world, this.getPos());
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return this.hasModule(IOModule.class) && this.getDescriptor() != null &&
                this.getModule(IOModule.class).hasCapability(capability, from, facing);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (this.hasModule(IOModule.class) && this.getDescriptor() != null)
        {
            T result = this.getModule(IOModule.class).getCapability(capability, from, facing);
            if (result != null)
                return result;
        }
        return null;
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

    @Override
    public EnumFacing getFacing()
    {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getPropertyKeys().contains(BlockMultiblockBase.FACING))
            return state.getValue(BlockMultiblockBase.FACING);
        else
            return EnumFacing.UP;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("gui." + this.descriptor.getName() + ".name");
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

    @Override
    public EventDispatcher getEventDispatcher()
    {
        if (this.eventDispatcher == null)
            this.initEventDispatcher();
        return this.eventDispatcher;
    }

    private void initEventDispatcher()
    {
        this.eventDispatcher = new EventDispatcher();
    }
}
