package net.qbar.common.tile.machine;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.QBarTileBase;
import net.qbar.common.util.FluidUtils;

public class TileTank extends QBarTileBase implements ITileMultiblockCore
{
    private BlockPos              inputPos;

    private final DirectionalTank tank;

    public TileTank()
    {
        this.tank = new DirectionalTank("TileTank", new FluidTank(Fluid.BUCKET_VOLUME * 16),
                new EnumFacing[] { EnumFacing.DOWN }, new EnumFacing[] { EnumFacing.UP });
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        this.tank.writeToNBT(tag);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.tank.readFromNBT(tag);

        super.readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        return this.getCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        if (this.tank.getFluidHandler(EnumFacing.UP) != null
                && this.tank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents() != null)
        {
            lines.add("Containing " + this.tank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents()
                    .getFluid().getName());
            lines.add(this.tank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getContents().amount + " / "
                    + this.tank.getFluidHandler(EnumFacing.UP).getTankProperties()[0].getCapacity() + " mB");
        }
    }

    public IFluidHandler getTank()
    {
        return this.tank.getInternalFluidHandler();
    }

    @Override
    public boolean isCore()
    {
        return true;
    }

    @Override
    public boolean isCorePresent()
    {
        return true;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && (from.equals(BlockPos.ORIGIN) || from.equals(this.getInputPos())) && facing.getAxis().isHorizontal())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                && (from.equals(BlockPos.ORIGIN) || from.equals(this.getInputPos())) && facing.getAxis().isHorizontal())
        {
            if (from.equals(BlockPos.ORIGIN))
                return (T) this.tank.getOutputHandler();
            return (T) this.tank.getInputHandler();
        }
        return super.getCapability(capability, facing);
    }

    private BlockPos getInputPos()
    {
        if (this.inputPos == null)
            this.inputPos = new BlockPos(0, 3, 0);
        return this.inputPos;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (FluidUtils.drainPlayerHand(this.getTank(), player) || FluidUtils.fillPlayerHand(this.getTank(), player))
            return true;
        return false;
    }
}