package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.event.TickHandler;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.ILoadable;
import net.qbar.common.util.FluidUtils;

import java.util.EnumMap;

public class TileSolarBoiler extends TileBoilerBase implements ILoadable
{
    private EnumMap<EnumFacing, TileSolarMirror> mirrors;

    public TileSolarBoiler()
    {
        super("solarboiler", 0, 3000, Fluid.BUCKET_VOLUME * 128, SteamUtil.BASE_PRESSURE * 2,
                Fluid.BUCKET_VOLUME * 144);

        this.mirrors = new EnumMap<>(EnumFacing.class);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isClient())
            return;
        float sunValue = getSunValue();

        int totalMirrorCount = this.mirrors.values().stream().mapToInt(mirror -> mirror.getMirrorCount()).sum();
        float producedHeat = (0.01f * totalMirrorCount) * sunValue;

        if (this.heat < this.getMaxHeat())
        {
            if (this.heat + producedHeat < this.getMaxHeat())
                this.heat += producedHeat;
            else
                this.heat = this.getMaxHeat();
            if (this.heat < this.getMinimumTemp())
                this.heat = this.getMinimumTemp();
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
            if (toProduce != 0 && this.world.getTotalWorldTime() % 5 == 0)
                this.heat--;
            this.sync();
        }
    }

    private float getSunValue()
    {
        float baseValue = 1;
        if (this.world.isRaining())
            baseValue -= 0.4f;
        if (this.world.isThundering())
            baseValue -= 0.3f;
        if (!this.world.isDaytime())
            baseValue = 0;
        return baseValue;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.SOLAR_BOILER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

            if (side.getPos().getX() == -1 && side.getPos().getY() == 0 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return true;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.SOLAR_BOILER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

            if (side.getPos().getX() == 1 && side.getPos().getY() == 0 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return true;
        }
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.SOLAR_BOILER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

            if (side.getPos().getX() == -1 && side.getPos().getY() == 0 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return (T) this.getSteamTank();
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            MultiblockSide side = QBarMachines.SOLAR_BOILER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

            if (side.getPos().getX() == 1 && side.getPos().getY() == 0 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return (T) this.getWaterTank();
        }
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

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        if (FluidUtils.drainPlayerHand(this.getWaterTank(), player)
                || FluidUtils.fillPlayerHand(this.getWaterTank(), player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(QBarConstants.MODINSTANCE, MachineGui.SOLARBOILER.getUniqueID(), this.getWorld(), this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
        else
            TickHandler.loadables.add(this);
    }

    @Override
    public void load()
    {
        this.checkMirrors();
    }

    public void checkMirrors()
    {
        this.mirrors.clear();
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos search = this.getPos().offset(facing, 2).up(3);
            if (this.world.getBlockState(search).getBlock() == QBarBlocks.SOLAR_MIRROR)
                this.mirrors.put(facing,
                        (TileSolarMirror) ((ITileMultiblock) this.world.getTileEntity(search)).getCore());
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return false;
    }
}
