package net.qbar.common.tile;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileOffshorePump extends QBarTileBase implements ITickable
{
    private final int     transferCapacity;
    private IFluidHandler top;
    private boolean       water = false;

    public TileOffshorePump(final int transferCapacity)
    {
        this.transferCapacity = transferCapacity;
    }

    @Override
    public void update()
    {
        if (this.world.getTotalWorldTime() % 40 == 0)
        {
            this.water = this.world.getBlockState(this.getPos().down()).getBlock().equals(Blocks.WATER);
            this.scanFluidHandler();
        }

        if (this.water)
            this.transferFluid();
    }

    public void transferFluid()
    {
        if (this.top != null)
            this.top.fill(new FluidStack(FluidRegistry.WATER, this.transferCapacity), true);
    }

    public void scanFluidHandler()
    {
        final BlockPos posNeighbor = this.getPos().up();
        final TileEntity tile = this.world.getTileEntity(posNeighbor);

        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN))
            this.top = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
        else if (this.top != null)
            this.top = null;
    }
}
