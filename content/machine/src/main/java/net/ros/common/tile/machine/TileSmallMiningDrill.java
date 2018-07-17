package net.ros.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.ros.common.ROSConstants;
import net.ros.common.block.BlockVeinOre;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.grid.node.IBelt;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.SteamUtil;
import net.ros.common.util.FluidUtils;

import java.util.Iterator;

public class TileSmallMiningDrill extends TileTickingModularMachine implements IContainerProvider
{
    @Getter
    @Setter
    private float heat, maxHeat;

    @Getter
    @Setter
    private boolean  completed;
    private BlockPos lastPos;

    private final float heatPerOperationTick = 30;
    private       int   tickBeforeHarvest;

    private       FluidStack             tempSludge;
    private final NonNullList<ItemStack> tempVarious;

    public TileSmallMiningDrill()
    {
        super(Machines.SMALL_MINING_DRILL);

        this.heat = 0;
        this.maxHeat = 3000;
        this.lastPos = this.getPos();

        this.tempVarious = NonNullList.create();
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 0));
        this.addModule(new SteamModule(this, SteamUtil::createTank));
        this.addModule(new FluidStorageModule(this)
                .addFilter("water", FluidUtils.WATER_FILTER));
        this.addModule(new IOModule(this));
    }

    @Override
    public void update()
    {
        super.update();

        if (this.isClient())
            return;

        boolean isDirty = false;

        ISteamTank steamTank = this.getModule(SteamModule.class).getInternalSteamHandler();
        SteamComponent steamComponent = this.getDescriptor().get(SteamComponent.class);

        FluidTank sludgeTank = (FluidTank) this.getModule(FluidStorageModule.class).getFluidHandler("sludge");

        if (tempSludge != null && sludgeTank.fill(tempSludge, false) == tempSludge.amount)
        {
            sludgeTank.fill(tempSludge, true);
            tempSludge = null;
        }

        if (!this.isCompleted() && tempSludge == null && this.tempVarious.isEmpty() && this.heat < this.maxHeat
                && steamTank.getSteam() >= steamComponent.getSteamConsumption())
        {
            BlockPos toCheck = this.lastPos;

            if (lastPos.equals(BlockPos.ORIGIN))
                toCheck = new BlockPos(this.getPos().getX() - 2, this.getPos().getY() - 1, this.getPos().getZ() - 2);
            else if (this.tickBeforeHarvest == 0)
            {
                if (toCheck.getX() == this.getPos().getX() + 2)
                {
                    if (toCheck.getZ() == this.getPos().getZ() + 2)
                    {
                        if (toCheck.getY() == 0)
                        {
                            this.setCompleted(true);
                        }
                        else
                            toCheck = new BlockPos(this.getPos().getX() - 2, toCheck.getY() - 1,
                                    this.getPos().getZ() - 2);
                    }
                    else
                        toCheck = new BlockPos(this.getPos().getX() - 2, toCheck.getY(), toCheck.getZ() + 1);
                }
                else
                    toCheck = new BlockPos(toCheck.getX() + 1, toCheck.getY(), toCheck.getZ());
                this.tickBeforeHarvest = (int) Math
                        .ceil(4 * (1 / (steamTank.getPressure() / steamComponent.getMaxPressureCapacity())));

                IBlockState state = this.world.getBlockState(toCheck);

                if (!this.world.isAirBlock(toCheck) && !(state.getBlock() instanceof IFluidBlock)
                        && state.getBlockHardness(world, toCheck) >= 0)
                {
                    if (state.getBlock() instanceof BlockVeinOre)
                    {
                        BlockVeinOre veinOre = (BlockVeinOre) state.getBlock();

                        tempSludge = new FluidStack(veinOre.getOreFromState(state).toSludge(),
                                veinOre.getRichnessFromState(state).getFluidAmount());
                        this.world.destroyBlock(toCheck, false);
                    }
                    else if (Math.abs(toCheck.getX() - this.getPos().getX()) < 2
                            && Math.abs(toCheck.getZ() - this.getPos().getZ()) < 2)
                    {
                        state.getBlock().getDrops(tempVarious, this.world, toCheck, state, 0);
                        this.world.destroyBlock(toCheck, false);
                    }
                    else
                        this.tickBeforeHarvest = 0;
                }
                else
                    this.tickBeforeHarvest = 0;
            }
            else
                this.tickBeforeHarvest--;

            lastPos = toCheck;

            this.heat += this.heatPerOperationTick * (steamTank.getPressure() / 2);

            steamTank.drainSteam((int) Math.max(steamComponent.getSteamConsumption() * steamTank.getPressure(),
                    steamComponent.getSteamConsumption()), true);
            isDirty = true;
        }
        if (!this.isCompleted())
        {
            IFluidTank fluidTank = (IFluidTank) this.getModule(FluidStorageModule.class).getFluidHandler("water");

            if (fluidTank.getFluidAmount() > 0)
            {
                int removable = Math.min(20, fluidTank.getFluidAmount());

                if (this.heat - removable <= this.getMinimumTemp())
                    removable = (int) (this.heat - this.getMinimumTemp());

                if (removable > 0)
                {
                    this.heat = this.heat - removable;
                    fluidTank.drain(removable, true);
                }
            }
        }
        if (!this.tempVarious.isEmpty())
        {
            if (this.tryInsertTrash(this.getFacing()))
                isDirty = true;
        }

        if (this.world.getTotalWorldTime() % 5 == 0)
        {
            if (this.heat > this.getMinimumTemp())
            {
                this.heat--;
                isDirty = true;
            }
            else if (this.heat < this.getMinimumTemp())
            {
                this.heat = this.getMinimumTemp();
                isDirty = true;
            }
        }

        if (isDirty)
            this.sync();
    }

    private int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getTemperature(this.pos) * 200);
    }

    private boolean tryInsertTrash(final EnumFacing facing)
    {
        TileEntity trashTile = this.world.getTileEntity(this.pos.offset(facing.getOpposite(), 2));
        if (trashTile instanceof IBelt)
        {
            final IBelt trashBelt = (IBelt) trashTile;

            Iterator<ItemStack> variousIterator = this.tempVarious.iterator();

            while (variousIterator.hasNext())
            {
                ItemStack next = variousIterator.next();
                if (trashBelt.insert(next, false))
                {
                    trashBelt.insert(next, true);
                    variousIterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("heat", this.heat);
        tag.setFloat("maxHeat", this.maxHeat);

        tag.setBoolean("completed", this.isCompleted());
        tag.setLong("lastPos", this.lastPos.toLong());
        tag.setInteger("tickBeforeHarvest", this.tickBeforeHarvest);

        tag.setTag("tempVarious", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.tempVarious));

        if (this.tempSludge != null)
            tag.setTag("tempSludge", this.tempSludge.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.heat = tag.getFloat("heat");
        this.maxHeat = tag.getFloat("maxHeat");

        this.completed = tag.getBoolean("completed");
        this.lastPos = BlockPos.fromLong(tag.getLong("lastPos"));
        this.tickBeforeHarvest = tag.getInteger("tickBeforeHarvest");

        ItemStackHelper.loadAllItems(tag.getCompoundTag("tempVarious"), this.tempVarious);

        if (tag.hasKey("tempSludge"))
            this.tempSludge = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("tempSludge"));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        SteamModule steamEngine = this.getModule(SteamModule.class);
        FluidStorageModule fluidStorage = this.getModule(FluidStorageModule.class);

        return new ContainerBuilder("smallminingdrill", player).player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .syncIntegerValue(steamEngine.getInternalSteamHandler()::getSteam,
                        steamEngine.getInternalSteamHandler()::setSteam)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("water"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("water"))::setFluid)
                .syncFluidValue(((FluidTank) fluidStorage.getFluidHandler("sludge"))::getFluid,
                        ((FluidTank) fluidStorage.getFluidHandler("sludge"))::setFluid)
                .addInventory().create();
    }

    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        IFluidHandler water = this.getModule(FluidStorageModule.class).getFluidHandler("water");
        if (FluidUtils.drainPlayerHand(water, player)
                || FluidUtils.fillPlayerHand(water, player))
        {
            this.markDirty();
            return true;
        }

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.SMALL_MINING_DRILL.getUniqueID(), this.world, this.pos
                        .getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
