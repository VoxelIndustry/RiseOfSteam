package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.QBarTileBase;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.FluidUtils;

import java.util.ArrayList;
import java.util.List;

public class TileSolarBoiler extends TileInventoryBase implements ITickable, IContainerProvider, ITileMultiblockCore
{
    private FluidTank fluidTank;
    private SteamTank steamTank;
    private float     heat;
    private int       maxHeat;

    public TileSolarBoiler()
    {
        super("solarboiler", 0);
        fluidTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 144,
                fluidStack -> fluidStack != null && fluidStack.getFluid() == (FluidRegistry.WATER));
        steamTank = new SteamTank(0, Fluid.BUCKET_VOLUME * 128, SteamUtil.AMBIANT_PRESSURE * 2);

        this.heat = 0;
        this.maxHeat = 3000;
    }

    @Override
    public void update()
    {

    }

    @Override
    public void addInfo(final List<String> lines)
    {
        if (this.getFluidTank().getFluid() != null)
        {
            lines.add("Containing " + this.getFluidTank().getFluid().getFluid().getName());
            lines.add(this.getFluidTank().getFluidAmount() + " / " + this.getFluidTank().getCapacity() + " mB");
        }
        lines.add("Heat " + this.heat + " / " + this.maxHeat);
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("waterTank", this.fluidTank.writeToNBT(new NBTTagCompound()));

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        tag.setFloat("heat", this.heat);
        return tag;
    }

    private ArrayList<MultiblockSide> tmpConnections = new ArrayList<>();

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));

        if (tag.hasKey("waterTank"))
            this.fluidTank.readFromNBT(tag.getCompoundTag("waterTank"));

        this.heat = tag.getFloat("heat");
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
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
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("solarboiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncFloatValue(this::getHeat, this::setHeat)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .syncFluidValue(this::getWater, this::setWater).addInventory().create();
    }

    public int getSteamAmount()
    {
        return this.getSteamTank().getSteam();
    }

    public void setSteamAmount(final int amount)
    {
        this.getSteamTank().setSteam(amount);
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }

    public FluidTank getFluidTank()
    {
        return this.fluidTank;
    }

    public FluidStack getWater()
    {
        return this.fluidTank.getFluid();
    }

    public void setWater(final FluidStack fluid)
    {
        this.fluidTank.setFluid(fluid);
    }

    public float getHeat()
    {
        return this.heat;
    }

    public void setHeat(final float heat)
    {
        this.heat = heat;
    }

    public int getMaxHeat()
    {
        return this.maxHeat;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        if (FluidUtils.drainPlayerHand(this.getFluidTank(), player)
                || FluidUtils.fillPlayerHand(this.getFluidTank(), player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBar.instance, EGui.SOLARBOILER.ordinal(), this.getWorld(), this.pos.getX(), this.pos.getY(),
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
