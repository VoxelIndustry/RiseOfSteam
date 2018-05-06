package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.grid.IConnectionAware;
import net.ros.common.grid.impl.CableGrid;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.steam.SteamStack;
import net.ros.common.steam.SteamUtil;
import net.ros.common.util.FluidUtils;
import net.ros.client.render.tile.VisibilityModelState;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.MultiblockSide;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.recipe.type.LiquidBoilerRecipe;
import net.ros.common.tile.module.SteamBoilerModule;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class TileLiquidBoiler extends TileTickingModularMachine implements IConnectionAware, IContainerProvider
{
    private static final Predicate<FluidStack> FUEL_FILTER = stack -> stack != null && stack.getFluid() != null &&
            RecipeHandler.inputMatchWithoutCount(RecipeHandler.LIQUIDBOILER_UID, 0, stack);

    private final ArrayList<MultiblockSide> connections;

    public TileLiquidBoiler()
    {
        super(Machines.LIQUID_BOILER);

        this.connections = new ArrayList<>();
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 0));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new FluidStorageModule(this)
                .addFilter("water", FluidUtils.WATER_FILTER)
                .addFilter("fuel", FUEL_FILTER));

        this.addModule(SteamBoilerModule.builder()
                .machine(this)
                .maxHeat(300).waterTank("water")
                .build());

        this.addModule(new IOModule(this));
    }

    private Fluid              cachedFluid;
    private LiquidBoilerRecipe recipe;
    private double             pendingFuel = 0;

    @Override
    public void update()
    {
        super.update();

        if (this.isServer())
        {
            IFluidTank fuelTank = (IFluidTank) this.getModule(FluidStorageModule.class).getFluidHandler("fuel");
            FluidStack fuel = fuelTank.getFluid();

            if (fuel != null && fuel.getFluid() != this.cachedFluid)
            {
                recipe = null;
                Optional<RecipeBase> recipeSearched =
                        RecipeHandler.getRecipe(RecipeHandler.LIQUIDBOILER_UID, fuel);

                recipeSearched.ifPresent(boilerRecipe -> recipe = (LiquidBoilerRecipe) boilerRecipe);
                this.cachedFluid = fuel.getFluid();
            }

            SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);

            if (recipe != null && boiler.getCurrentHeat() < boiler.getMaxHeat())
            {
                float toConsume = 1000f / recipe.getTime();
                this.pendingFuel += toConsume;

                toConsume = Math.min((int) pendingFuel, fuelTank.getFluidAmount());

                boiler.addHeat(
                        recipe.getRecipeOutputs(SteamStack.class).get(0).getRaw().getAmount() * toConsume);
                fuelTank.drain((int) toConsume, true);
                this.pendingFuel -= toConsume;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

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
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);
        SteamBoilerModule boiler = this.getModule(SteamBoilerModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("liquidboiler", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .syncFloatValue(boiler::getCurrentHeat, boiler::setCurrentHeat)
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("water"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("water"))::setFluid)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("fuel"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("fuel"))::setFluid)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        if (from.getY() == 0)
        {
            IFluidHandler fuelTank = this.getModule(FluidStorageModule.class).getFluidHandler("fuel");

            if (FluidUtils.drainPlayerHand(fuelTank, player) || FluidUtils.fillPlayerHand(fuelTank, player))
            {
                this.markDirty();
                return true;
            }
        }
        else
        {
            IFluidHandler waterTank = this.getModule(FluidStorageModule.class).getFluidHandler("water");

            if (FluidUtils.drainPlayerHand(waterTank, player) || FluidUtils.fillPlayerHand(waterTank, player))
            {
                this.markDirty();
                return true;
            }
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.LIQUIDBOILER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
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
        this.connections.add(Machines.LIQUID_BOILER.get(MultiblockComponent.class)
                .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        this.updateState();
    }

    public void disconnectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {
        this.connections.remove(Machines.LIQUID_BOILER.get(MultiblockComponent.class)
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
}