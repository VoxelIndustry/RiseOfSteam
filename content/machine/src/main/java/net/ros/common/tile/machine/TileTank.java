package net.ros.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.grid.IConnectionAware;
import net.ros.common.grid.impl.CableGrid;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.util.FluidUtils;
import net.ros.client.render.tile.VisibilityModelState;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.MultiblockSide;

import java.util.ArrayList;

public class TileTank extends TileModularMachine implements IContainerProvider, IConnectionAware
{
    @Getter
    private int tier;

    private final ArrayList<MultiblockSide> connections = new ArrayList<>();

    public TileTank(int tier)
    {
        super(tier == 0 ? Machines.SMALL_FLUID_TANK :
                (tier == 1 ? Machines.MEDIUM_FLUID_TANK : Machines.BIG_FLUID_TANK));
        this.tier = tier;
    }

    public TileTank()
    {
        this.tier = -1;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 0));
        this.addModule(new FluidStorageModule(this));
        this.addModule(new IOModule(this));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("tier", this.tier);

        if (this.isServer())
        {
            for (MultiblockSide side : this.connections)
            {
                tag.setLong("connPos" + this.connections.indexOf(side), side.getPos().toLong());
                tag.setByte("connFacing" + this.connections.indexOf(side), (byte) side.getFacing().ordinal());
            }
            tag.setInteger("connLength", this.connections.size());
        }
        return super.writeToNBT(tag);
    }

    private ArrayList<MultiblockSide> tmpConnections = new ArrayList<>();

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.tier = tag.getInteger("tier");

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

        super.readFromNBT(tag);
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

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler fluid = this.getModule(FluidStorageModule.class).getFluidHandler("fluid");
        if (FluidUtils.drainPlayerHand(fluid, player) || FluidUtils.fillPlayerHand(fluid, player))
        {
            this.markDirty();
            return true;
        }
        player.openGui(ROSConstants.MODINSTANCE, MachineGui.FLUIDTANK.getUniqueID(), this.world, this.pos.getX(),
                this.pos.getY(), this.pos.getZ());
        return false;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("fluidtank", player)
                .player(player).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("fluid"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("fluid"))::setFluid)
                .addInventory().create();
    }

    public void connectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {
        if (tier == 0)
            this.connections.add(Machines.SMALL_FLUID_TANK.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        else if (tier == 1)
            this.connections.add(Machines.MEDIUM_FLUID_TANK.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        else
            this.connections.add(Machines.BIG_FLUID_TANK.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        this.updateState();
    }

    public void disconnectTrigger(BlockPos from, EnumFacing facing, CableGrid grid)
    {
        if (tier == 0)
            this.connections.remove(Machines.SMALL_FLUID_TANK.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        else if (tier == 1)
            this.connections.remove(Machines.MEDIUM_FLUID_TANK.get(MultiblockComponent.class)
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing()));
        else
            this.connections.remove(Machines.BIG_FLUID_TANK.get(MultiblockComponent.class)
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

        if (this.getTier() == 1)
        {
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.SOUTH)))
            {
                this.state.parts.add("OutputB N");
                this.state.parts.add("Output1B N");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, 1), EnumFacing.SOUTH)))
            {
                this.state.parts.add("OutputA N");
                this.state.parts.add("Output1A N");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.EAST)))
            {
                this.state.parts.add("OutputA O");
                this.state.parts.add("Output1A O");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 0), EnumFacing.EAST)))
            {
                this.state.parts.add("OutputB O");
                this.state.parts.add("Output1B O");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 0), EnumFacing.NORTH)))
            {
                this.state.parts.add("OutputA S");
                this.state.parts.add("Output1A S");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN, EnumFacing.NORTH)))
            {
                this.state.parts.add("OutputB S");
                this.state.parts.add("Output1B S");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN, EnumFacing.WEST)))
            {
                this.state.parts.add("OutputA E");
                this.state.parts.add("Output1A E");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, 1), EnumFacing.WEST)))
            {
                this.state.parts.add("OutputB E");
                this.state.parts.add("Output1B E");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 2, 0), EnumFacing.UP)))
            {
                this.state.parts.add("Input1 B");
                this.state.parts.add("Input2 B");
                this.state.parts.add("Input4 B");
                this.state.parts.add("Input5 B");
                this.state.parts.add("Boulon B");
                this.state.parts.add("Boulon1 B");
                this.state.parts.add("Boulon2 B");
                this.state.parts.add("Boulon3 B");
                this.state.parts.add("Boulon4 B");
                this.state.parts.add("Boulon5 B");
                this.state.parts.add("Boulon6 B");
                this.state.parts.add("Boulon7 B");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 2, 0), EnumFacing.UP)))
            {
                this.state.parts.add("Input1 A");
                this.state.parts.add("Input2 A");
                this.state.parts.add("Input4 A");
                this.state.parts.add("Input5 A");
                this.state.parts.add("Boulon A");
                this.state.parts.add("Boulon1 A");
                this.state.parts.add("Boulon2 A");
                this.state.parts.add("Boulon3 A");
                this.state.parts.add("Boulon4 A");
                this.state.parts.add("Boulon5 A");
                this.state.parts.add("Boulon6 A");
                this.state.parts.add("Boulon7 A");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 2, 1), EnumFacing.UP)))
            {
                this.state.parts.add("Input1 C");
                this.state.parts.add("Input2 C");
                this.state.parts.add("Input4 C");
                this.state.parts.add("Input5 C");
                this.state.parts.add("Boulon C");
                this.state.parts.add("Boulon1 C");
                this.state.parts.add("Boulon2 C");
                this.state.parts.add("Boulon3 C");
                this.state.parts.add("Boulon4 C");
                this.state.parts.add("Boulon5 C");
                this.state.parts.add("Boulon6 C");
                this.state.parts.add("Boulon7 C");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 2, 1), EnumFacing.UP)))
            {
                this.state.parts.add("Input1 D");
                this.state.parts.add("Input2 D");
                this.state.parts.add("Input4 D");
                this.state.parts.add("Input5 D");
                this.state.parts.add("Boulon D");
                this.state.parts.add("Boulon1 D");
                this.state.parts.add("Boulon2 D");
                this.state.parts.add("Boulon3 D");
                this.state.parts.add("Boulon4 D");
                this.state.parts.add("Boulon5 D");
                this.state.parts.add("Boulon6 D");
                this.state.parts.add("Boulon7 D");
            }
        }
        else
        {
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, 1), EnumFacing.SOUTH)))
            {
                this.state.parts.add("OutputN A");
                this.state.parts.add("Output1N A");
                if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, 1), EnumFacing.WEST)))
                    this.state.parts.add("Output2N A");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, 1), EnumFacing.SOUTH)))
            {
                this.state.parts.add("OutputN B");
                this.state.parts.add("Output1N B");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.SOUTH)))
            {
                this.state.parts.add("OutputN C");
                this.state.parts.add("Output1N C");
                if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.EAST)))
                    this.state.parts.add("Output2N C");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 1), EnumFacing.EAST)))
            {
                this.state.parts.add("OutputO A");
                this.state.parts.add("Output1O A");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, 0), EnumFacing.EAST)))
            {
                this.state.parts.add("OutputO B");
                this.state.parts.add("Output1O B");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, -1), EnumFacing.EAST)))
            {
                this.state.parts.add("OutputO C");
                this.state.parts.add("Output1O C");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, -1), EnumFacing.NORTH)))
            {
                this.state.parts.add("OutputS A");
                this.state.parts.add("Output1S A");
                if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 0, -1), EnumFacing.EAST)))
                    this.state.parts.add("Output2S A");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 0, -1), EnumFacing.NORTH)))
            {
                this.state.parts.add("OutputS B");
                this.state.parts.add("Output1S B");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, -1), EnumFacing.NORTH)))
            {
                this.state.parts.add("OutputS C");
                this.state.parts.add("Output1S C");
                if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, -1), EnumFacing.WEST)))
                    this.state.parts.add("Output2S C");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, -1), EnumFacing.WEST)))
            {
                this.state.parts.add("OutputE A");
                this.state.parts.add("Output1E A");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, 0), EnumFacing.WEST)))
            {
                this.state.parts.add("OutputE B");
                this.state.parts.add("Output1E B");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 0, 1), EnumFacing.WEST)))
            {
                this.state.parts.add("OutputE C");
                this.state.parts.add("Output1E C");
            }

            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 3, 0), EnumFacing.UP)))
            {
                this.state.parts.add("Input Top");
                this.state.parts.add("Input Top1");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 3, 0), EnumFacing.UP)))
            {
                this.state.parts.add("Input O");
                this.state.parts.add("Input O 1");
                this.state.parts.add("Input O 2");
                this.state.parts.add("Input O 3");
                this.state.parts.add("Boulon O");
                this.state.parts.add("Boulon O 1");
                this.state.parts.add("Boulon O 2");
                this.state.parts.add("Boulon O 3");
                this.state.parts.add("Boulon O 4");
                this.state.parts.add("Boulon O 5");
                this.state.parts.add("Boulon O 6");
                this.state.parts.add("Boulon O 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 3, 0), EnumFacing.UP)))
            {
                this.state.parts.add("Input E");
                this.state.parts.add("Input E 1");
                this.state.parts.add("Input E 2");
                this.state.parts.add("Input E 3");
                this.state.parts.add("Boulon E");
                this.state.parts.add("Boulon E 1");
                this.state.parts.add("Boulon E 2");
                this.state.parts.add("Boulon E 3");
                this.state.parts.add("Boulon E 4");
                this.state.parts.add("Boulon E 5");
                this.state.parts.add("Boulon E 6");
                this.state.parts.add("Boulon E 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 3, -1), EnumFacing.UP)))
            {
                this.state.parts.add("Input S");
                this.state.parts.add("Input S 1");
                this.state.parts.add("Input S 2");
                this.state.parts.add("Input S 3");
                this.state.parts.add("Boulon S");
                this.state.parts.add("Boulon S 1");
                this.state.parts.add("Boulon S 2");
                this.state.parts.add("Boulon S 3");
                this.state.parts.add("Boulon S 4");
                this.state.parts.add("Boulon S 5");
                this.state.parts.add("Boulon S 6");
                this.state.parts.add("Boulon S 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(0, 3, 1), EnumFacing.UP)))
            {
                this.state.parts.add("Input N");
                this.state.parts.add("Input N 1");
                this.state.parts.add("Input N 2");
                this.state.parts.add("Input N 3");
                this.state.parts.add("Boulon N");
                this.state.parts.add("Boulon N 1");
                this.state.parts.add("Boulon N 2");
                this.state.parts.add("Boulon N 3");
                this.state.parts.add("Boulon N 4");
                this.state.parts.add("Boulon N 5");
                this.state.parts.add("Boulon N 6");
                this.state.parts.add("Boulon N 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 3, 1), EnumFacing.UP)))
            {
                this.state.parts.add("Input NE");
                this.state.parts.add("Input NE 1");
                this.state.parts.add("Input NE 2");
                this.state.parts.add("Input NE 3");
                this.state.parts.add("Input NE 4");
                this.state.parts.add("Boulon NE");
                this.state.parts.add("Boulon NE 1");
                this.state.parts.add("Boulon NE 2");
                this.state.parts.add("Boulon NE 3");
                this.state.parts.add("Boulon NE 4");
                this.state.parts.add("Boulon NE 5");
                this.state.parts.add("Boulon NE 6");
                this.state.parts.add("Boulon NE 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 3, 1), EnumFacing.UP)))
            {
                this.state.parts.add("Input NO");
                this.state.parts.add("Input NO 1");
                this.state.parts.add("Input NO 2");
                this.state.parts.add("Input NO 3");
                this.state.parts.add("Input NO 4");
                this.state.parts.add("Boulon NO");
                this.state.parts.add("Boulon NO 1");
                this.state.parts.add("Boulon NO 2");
                this.state.parts.add("Boulon NO 3");
                this.state.parts.add("Boulon NO 4");
                this.state.parts.add("Boulon NO 5");
                this.state.parts.add("Boulon NO 6");
                this.state.parts.add("Boulon NO 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(-1, 3, -1), EnumFacing.UP)))
            {
                this.state.parts.add("Input SE");
                this.state.parts.add("Input SE 1");
                this.state.parts.add("Input SE 2");
                this.state.parts.add("Input SE 3");
                this.state.parts.add("Input SE 4");
                this.state.parts.add("Boulon SE");
                this.state.parts.add("Boulon SE 1");
                this.state.parts.add("Boulon SE 2");
                this.state.parts.add("Boulon SE 3");
                this.state.parts.add("Boulon SE 4");
                this.state.parts.add("Boulon SE 5");
                this.state.parts.add("Boulon SE 6");
                this.state.parts.add("Boulon SE 7");
            }
            if (!this.connections.contains(new MultiblockSide(BlockPos.ORIGIN.add(1, 3, -1), EnumFacing.UP)))
            {
                this.state.parts.add("Input SO");
                this.state.parts.add("Input SO 1");
                this.state.parts.add("Input SO 2");
                this.state.parts.add("Input SO 3");
                this.state.parts.add("Input SO 4");
                this.state.parts.add("Boulon SO");
                this.state.parts.add("Boulon SO 1");
                this.state.parts.add("Boulon SO 2");
                this.state.parts.add("Boulon SO 3");
                this.state.parts.add("Boulon SO 4");
                this.state.parts.add("Boulon SO 5");
                this.state.parts.add("Boulon SO 6");
                this.state.parts.add("Boulon SO 7");
            }
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }
}
