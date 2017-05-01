package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.util.FluidUtils;

public class TileBoiler extends TileBoilerBase
{
    private int currentBurnTime;
    private int maxBurnTime;

    public TileBoiler()
    {
        super("TileBoiler", 1, 3000, 4000, SteamUtil.AMBIANT_PRESSURE * 2, Fluid.BUCKET_VOLUME * 32);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isServer())
        {
            if (this.maxBurnTime == 0 && !this.getStackInSlot(0).isEmpty())
            {
                this.maxBurnTime = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(0)) / 2;
                if (this.maxBurnTime != 0)
                    this.decrStackSize(0, 1);
            }
            if (this.currentBurnTime < this.maxBurnTime)
            {
                this.currentBurnTime++;
                this.heat++;
            }
            else
            {
                this.currentBurnTime = 0;
                this.maxBurnTime = 0;
            }

            if (this.heat >= 900)
            {
                int toProduce = (int) (1 / Math.E * (this.heat / 100));
                final FluidStack drained = this.getWaterTank().drain(toProduce, true);
                if (drained != null)
                    toProduce = drained.amount;
                else
                    toProduce = 0;
                this.getSteamTank().fillSteam(toProduce, true);
                if (toProduce != 0 && this.world.getTotalWorldTime() % 2 == 0)
                    this.heat--;
            }

            if (this.world.getTotalWorldTime() % 5 == 0)
            {
                if (this.heat > this.getMinimumTemp())
                    this.heat--;
                else if (this.heat < this.getMinimumTemp())
                    this.heat = this.getMinimumTemp();
            }
            //this.sync();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("currentBurnTime", this.currentBurnTime);
        tag.setInteger("maxBurnTime", this.maxBurnTime);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentBurnTime = tag.getInteger("currentBurnTime");
        this.maxBurnTime = tag.getInteger("maxBurnTime");
    }

    public int getCurrentBurnTime()
    {
        return this.currentBurnTime;
    }

    public void setCurrentBurnTime(final int currentBurnTime)
    {
        this.currentBurnTime = currentBurnTime;
    }

    public int getMaxBurnTime()
    {
        return this.maxBurnTime;
    }

    public void setMaxBurnTime(final int maxBurnTime)
    {
        this.maxBurnTime = maxBurnTime;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("boiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).slot(0, 80, 43).syncFloatValue(this::getHeat, this::setHeat)
                .syncIntegerValue(this::getMaxBurnTime, this::setMaxBurnTime)
                .syncIntegerValue(this::getCurrentBurnTime, this::setCurrentBurnTime)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .syncFluidValue(this::getWater, this::setWater).addInventory().create();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.SOLID_BOILER.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return true;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 0)
                return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.SOLID_BOILER.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return (T) this.getSteamTank();
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.EAST && side.getPos().getX() == 1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 0)
                return (T) this.getWaterTank();
        }
        return null;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if(player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        if (FluidUtils.drainPlayerHand(this.getWaterTank(), player)
                || FluidUtils.fillPlayerHand(this.getWaterTank(), player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBar.instance, EGui.BOILER.ordinal(), this.getWorld(), this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }
}
