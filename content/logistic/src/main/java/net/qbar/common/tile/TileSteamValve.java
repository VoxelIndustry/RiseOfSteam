package net.qbar.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.block.BlockSteamValve;
import net.qbar.common.grid.CableGrid;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.grid.SteamGrid;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamTank;
import net.qbar.common.steam.SteamTank;

import javax.annotation.Nullable;

public class TileSteamValve extends QBarTileBase implements IConnectionAware
{
    private static final SteamTank      emptyTank    = new SteamTank(0, 0, 0);
    private              DummySteamTank forwardTank  = new DummySteamTank(emptyTank);
    private              DummySteamTank backwardTank = new DummySteamTank(emptyTank);

    @Getter
    @Setter
    private EnumFacing facing;

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing.getAxis() == this.getAxis())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing.getAxis() == this.getAxis())
        {
            if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
                return CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY.cast(this.backwardTank);
            else
                return CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY.cast(this.forwardTank);
        }
        return super.getCapability(capability, facing);
    }

    private SteamGrid getSteamGrid(boolean forward)
    {
        return null;
    }

    private EnumFacing.Axis getAxis()
    {
        return this.world.getBlockState(this.pos).getValue(BlockSteamValve.AXIS);
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
            this.forwardTank.setDelegate(((SteamGrid) grid).getTank());
        else
            this.backwardTank.setDelegate(((SteamGrid) grid).getTank());
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE)
            this.forwardTank.setDelegate(((SteamGrid) grid).getTank());
        else
            this.backwardTank.setDelegate(((SteamGrid) grid).getTank());
    }

    private class DummySteamTank implements ISteamTank
    {
        @Getter
        @Setter
        private SteamTank delegate;

        public DummySteamTank(SteamTank delegate)
        {
            this.delegate = delegate;
        }

        @Override
        public int getSteam()
        {
            return delegate.getSteam();
        }

        @Override
        public int getCapacity()
        {
            return delegate.getCapacity();
        }

        @Override
        public float getMaxPressure()
        {
            return delegate.getMaxPressure();
        }

        @Override
        public int drainSteam(int amount, boolean doDrain)
        {
            return delegate.drainSteam(amount, doDrain);
        }

        @Override
        public int fillSteam(int amount, boolean doFill)
        {
            return delegate.fillSteam(amount, doFill);
        }

        @Override
        public float getPressure()
        {
            return delegate.getPressure();
        }

        @Override
        public FluidStack toFluidStack()
        {
            return delegate.toFluidStack();
        }

        @Override
        public boolean canFill()
        {
            return delegate.canFill();
        }

        @Override
        public boolean canDrain()
        {
            return delegate.canDrain();
        }
    }
}
