package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.grid.impl.CableGrid;
import net.qbar.common.grid.IConnectionAware;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.type.LiquidBoilerRecipe;
import net.qbar.common.steam.SteamCapabilities;
import net.qbar.common.steam.SteamStack;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.util.FluidUtils;

import java.util.ArrayList;
import java.util.Optional;

public class TileLiquidBoiler extends TileBoilerBase implements IConnectionAware
{
    private final FluidTank fuelTank;

    private final ArrayList<MultiblockSide> connections;

    public TileLiquidBoiler()
    {
        super(QBarMachines.LIQUID_BOILER, 0, 300, Fluid.BUCKET_VOLUME * 32, SteamUtil.BASE_PRESSURE * 2, Fluid.BUCKET_VOLUME * 64);

        this.fuelTank = new FilteredFluidTank(Fluid.BUCKET_VOLUME * 48,
                fluidStack -> fluidStack != null && fluidStack.getFluid() != (FluidRegistry.WATER));
        this.connections = new ArrayList<>();
    }

    private Fluid              cachedFluid;
    private LiquidBoilerRecipe recipe;
    private double pendingFuel = 0;

    @Override
    public void update()
    {
        super.update();

        if (this.isServer())
        {
            if (this.getFuel() != null && this.getFuel().getFluid() != this.cachedFluid)
            {
                recipe = null;
                Optional<QBarRecipe> recipeSearched = QBarRecipeHandler.getRecipe(QBarRecipeHandler.LIQUIDBOILER_UID,
                        this.getFuel());

                recipeSearched.ifPresent(qBarRecipe -> recipe = (LiquidBoilerRecipe) qBarRecipe);
                this.cachedFluid = this.getFuel().getFluid();
            }

            if (recipe != null)
            {
                double toConsume = 1000.0 / recipe.getTime();
                this.pendingFuel += toConsume;

                toConsume = Math.min((int) pendingFuel, this.getFuelTank().getFluidAmount());

                this.heat += recipe.getRecipeOutputs(SteamStack.class).get(0).getRawIngredient().getAmount()
                        * toConsume;
                this.getFuelTank().drain((int) toConsume, true);
                this.pendingFuel -= toConsume;
            }

            if (this.heat >= 100)
            {
                int toProduce = (int) (1 / Math.E * (this.heat / 10));
                final FluidStack drained = this.getWaterTank().drain(toProduce, true);
                if (drained != null)
                    toProduce = drained.amount;
                else
                    toProduce = 0;
                this.getSteamTank().fillSteam(toProduce, true);
                if (toProduce != 0 && this.world.getTotalWorldTime() % 2 == 0)
                    this.heat--;
                this.sync();
            }

            if (this.world.getTotalWorldTime() % 5 == 0)
            {
                if (this.heat > this.getMinimumTemp())
                    this.heat -= 0.1f;
                else if (this.heat < this.getMinimumTemp())
                    this.heat = this.getMinimumTemp();
                this.sync();
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));

        if (this.isServer())
        {
            for (MultiblockSide side : this.connections)
            {
                tag.setLong("connPos" + this.connections.indexOf(side), side.getPos().toLong());
                tag.setByte("connFacing" + this.connections.indexOf(side), (byte) side.getFacing().ordinal());
            }
            tag.setInteger("connLength", this.connections.size());
        }
        return tag;
    }

    private ArrayList<MultiblockSide> tmpConnections = new ArrayList<>();

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("fuelTank"))
            this.fuelTank.readFromNBT(tag.getCompoundTag("fuelTank"));

        if (this.isClient())
        {
            tmpConnections.clear();
            tmpConnections.addAll(this.connections);
            this.connections.clear();

            for (int i = 0; i < tag.getInteger("connLength"); i++)
                this.connections.add(new MultiblockSide(BlockPos.fromLong(tag.getLong("connPos" + i)),
                        EnumFacing.values()[tag.getByte("connFacing" + i)]));

            if (tmpConnections.size() != this.connections.size() || !tmpConnections.equals(this.connections))
                this.updateState();
        }
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == SteamCapabilities.STEAM_HANDLER && from.getY() == 2
                && facing == EnumFacing.UP)
            return true;
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && from.getY() == 0
                && facing.getAxis().isHorizontal())
            return true;
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == SteamCapabilities.STEAM_HANDLER && from.getY() == 2
                && facing == EnumFacing.UP)
            return SteamCapabilities.STEAM_HANDLER.cast(this.getSteamTank());
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && from.getY() == 0)
        {
            MultiblockSide side = QBarMachines.LIQUID_BOILER.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getFacing() == EnumFacing.NORTH)
            {
                if (side.getPos().equals(BlockPos.ORIGIN))
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getWaterTank());
                else if (side.getPos().getX() == 1 && side.getPos().getZ() == 0)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getFuelTank());
            }
            else if (side.getFacing() == EnumFacing.WEST)
            {
                if (side.getPos().getX() == 0 && side.getPos().getZ() == 1)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getWaterTank());
                else if (side.getPos().equals(BlockPos.ORIGIN))
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getFuelTank());
            }
            else if (side.getFacing() == EnumFacing.SOUTH)
            {
                if (side.getPos().getX() == 1 && side.getPos().getZ() == 1)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getWaterTank());
                else if (side.getPos().getX() == 0 && side.getPos().getZ() == 1)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getFuelTank());
            }
            else if (side.getFacing() == EnumFacing.EAST)
            {
                if (side.getPos().getX() == 1 && side.getPos().getZ() == 0)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getWaterTank());
                else if (side.getPos().getX() == 1 && side.getPos().getZ() == 1)
                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.getFuelTank());
            }
        }
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("liquidboiler", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncFloatValue(this::getHeat, this::setHeat)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .syncFluidValue(this::getWater, this::setWater).syncFluidValue(this::getFuel, this::setFuel)
                .addInventory().create();
    }

    public FluidTank getFuelTank()
    {
        return this.fuelTank;
    }

    public FluidStack getFuel()
    {
        return this.fuelTank.getFluid();
    }

    public void setFuel(final FluidStack fluid)
    {
        this.fuelTank.setFluid(fluid);
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        if (from.getY() == 0)
        {
            if (FluidUtils.drainPlayerHand(this.getFuelTank(), player)
                    || FluidUtils.fillPlayerHand(this.getFuelTank(), player))
            {
                this.markDirty();
                return true;
            }
        }
        else
        {
            if (FluidUtils.drainPlayerHand(this.getWaterTank(), player)
                    || FluidUtils.fillPlayerHand(this.getWaterTank(), player))
            {
                this.markDirty();
                return true;
            }
        }
        player.openGui(QBarConstants.MODINSTANCE, MachineGui.LIQUIDBOILER.getUniqueID(), this.getWorld(), this.pos
                        .getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }

    public void connectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {
        this.connections.add(QBarMachines.LIQUID_BOILER.get(MultiblockComponent.class)
                .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        this.updateState();
    }

    public void disconnectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {
        this.connections.remove(QBarMachines.LIQUID_BOILER.get(MultiblockComponent.class)
                .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        this.updateState();
    }

    @Override
    public void connectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.connectTrigger(BlockPos.ORIGIN, facing, grid);
    }

    @Override
    public void disconnectTrigger(EnumFacing facing, CableGrid grid)
    {
        this.disconnectTrigger(BlockPos.ORIGIN, facing, grid);
    }

    ////////////
    // RENDER //
    ////////////

    public final VisibilityModelState state = new VisibilityModelState();

    private void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }
        this.state.parts.clear();

        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, 1), EnumFacing.SOUTH)))
        {
            this.state.parts.add("InputA E");
            this.state.parts.add("Input1A E");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.SOUTH)))
        {
            this.state.parts.add("InputB E");
            this.state.parts.add("Input1B E");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.EAST)))
        {
            this.state.parts.add("InputA S");
            this.state.parts.add("Input1A S");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 0), EnumFacing.EAST)))
        {
            this.state.parts.add("InputB S");
            this.state.parts.add("Input1B S");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 0), EnumFacing.NORTH)))
        {
            this.state.parts.add("InputA O");
            this.state.parts.add("Input1A O");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN, EnumFacing.NORTH)))
        {
            this.state.parts.add("InputB O");
            this.state.parts.add("Input1B O");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN, EnumFacing.WEST)))
        {
            this.state.parts.add("InputA N");
            this.state.parts.add("Input1A N");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, 1), EnumFacing.WEST)))
        {
            this.state.parts.add("InputB N");
            this.state.parts.add("Input1B N");
        }

        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 2, 0), EnumFacing.UP)))
        {
            this.state.parts.add("OutputD");
            this.state.parts.add("OutputD1");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 2, 0), EnumFacing.UP)))
        {
            this.state.parts.add("OutputC");
            this.state.parts.add("OutputC1");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 2, 1), EnumFacing.UP)))
        {
            this.state.parts.add("OutputB");
            this.state.parts.add("OutputB1");
        }
        if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 2, 1), EnumFacing.UP)))
        {
            this.state.parts.add("OutputA");
            this.state.parts.add("OutputA1");
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
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