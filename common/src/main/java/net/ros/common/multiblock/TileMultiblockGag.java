package net.ros.common.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.grid.IConnectionAware;
import net.ros.common.grid.impl.CableGrid;
import net.ros.common.tile.ITileInfoProvider;
import net.ros.common.tile.TileBase;

import javax.annotation.Nullable;
import java.util.List;

public class TileMultiblockGag extends TileBase implements ITileMultiblock, ITileInfoProvider, IConnectionAware
{
    private BlockPos corePos = this.pos;

    private BlockPos coreOffset = null;

    public TileMultiblockGag()
    {

    }

    @Override
    public void addInfo(final List<String> lines)
    {
        if (this.isCorePresent() && this.getCore() instanceof ITileInfoProvider)
            ((ITileInfoProvider) this.getCore()).addInfo(lines);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("corePosX", this.corePos.getX());
        tag.setInteger("corePosY", this.corePos.getY());
        tag.setInteger("corePosZ", this.corePos.getZ());

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.corePos = new BlockPos(tag.getInteger("corePosX"), tag.getInteger("corePosY"), tag.getInteger("corePosZ"));

        super.readFromNBT(tag);
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing)
    {
        if (this.isCorePresent())
            return this.getCore().hasCapability(capability, this.getCoreOffset(), facing);
        return super.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing)
    {
        if (this.isCorePresent())
            return this.getCore().getCapability(capability, this.getCoreOffset(), facing);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isCore()
    {
        return false;
    }

    @Override
    public boolean isCorePresent()
    {
        return this.world.getTileEntity(this.getCorePos()) != null
                && this.world.getTileEntity(this.getCorePos()) instanceof ITileMultiblockCore;
    }

    @Override
    public void breakCore()
    {
        if (this.isCorePresent())
            this.getCore().breakCore();
        this.world.destroyBlock(this.getPos(), false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.corePos;
    }

    public void setCorePos(final BlockPos pos)
    {
        this.corePos = pos;
    }

    @Override
    public ITileMultiblockCore getCore()
    {
        return (ITileMultiblockCore) this.world.getTileEntity(this.getCorePos());
    }

    public BlockPos getCoreOffset()
    {
        if (this.coreOffset == null)
            this.coreOffset = this.pos.subtract(this.corePos);
        return this.coreOffset;
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.getCore().connectTrigger(this.getCoreOffset(), facing, grid);
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.getCore().disconnectTrigger(this.getCoreOffset(), facing, grid);
    }
}
