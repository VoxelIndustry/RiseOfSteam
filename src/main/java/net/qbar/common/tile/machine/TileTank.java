package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.DirectionalTank;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.FluidUtils;

import java.util.List;

public class TileTank extends TileInventoryBase implements ITileMultiblockCore, IContainerProvider
{
    private BlockPos              inputPos;

    private final DirectionalTank tank;
    private int                   tier;

    public TileTank(final int capacity, int tier)
    {
        super("fluidtank", 0);
        this.tank = new DirectionalTank("TileTank", new FluidTank(capacity), new EnumFacing[] { EnumFacing.DOWN },
                new EnumFacing[] { EnumFacing.UP });
        this.tier = tier;
    }

    public TileTank()
    {
        this(0, 0);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        this.tank.writeToNBT(tag);
        tag.setInteger("tier", this.tier);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.tank.readFromNBT(tag);
        this.tier = tag.getInteger("tier");

        super.readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

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

    public FluidTank getTank()
    {
        return this.tank.getInternalFluidHandler();
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
        if (this.tier == 0)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                    && (from.equals(BlockPos.ORIGIN) || from.equals(this.getInputPos()))
                    && facing.getAxis().isHorizontal())
                return true;
        }
        else
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                    && ((from.getY() == 0 && facing.getAxis().isHorizontal()) || facing.getAxis().isVertical()))
                return true;
        }
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (this.tier == 0)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                    && (from.equals(BlockPos.ORIGIN) || from.equals(this.getInputPos()))
                    && facing.getAxis().isHorizontal())
            {
                if (from.equals(BlockPos.ORIGIN))
                    return (T) this.tank.getOutputHandler();
                return (T) this.tank.getInputHandler();
            }
        }
        else
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                    && ((from.getY() == 0 && facing.getAxis().isHorizontal()) || facing.getAxis().isVertical()))
            {
                if (from.getY() == 0)
                    return (T) this.tank.getOutputHandler();
                return (T) this.tank.getInputHandler();
            }
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
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        if (FluidUtils.drainPlayerHand(this.getTank(), player) || FluidUtils.fillPlayerHand(this.getTank(), player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBar.instance, EGui.FLUIDTANK.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return false;
    }

    public FluidStack getFluid()
    {
        return this.tank.getInternalFluidHandler().getTankProperties()[0].getContents();
    }

    public void setFluid(final FluidStack fluid)
    {
        this.tank.setFluidStack(fluid);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("fluidtank", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncFluidValue(this::getFluid, this::setFluid).addInventory().create();
    }
}