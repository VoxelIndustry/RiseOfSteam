package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.qbar.QBar;
import net.qbar.common.block.BlockVeinOre;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.grid.IBelt;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.SteamComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.ore.SludgeData;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.tile.TileMultiblockInventoryBase;
import net.qbar.common.util.FluidUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

public class TileSmallMiningDrill extends TileMultiblockInventoryBase implements ITickable
{
    private final SteamComponent steamMachine = QBarMachines.SMALL_MINING_DRILL.get(SteamComponent.class);

    @Getter
    private SteamTank steamTank;
    @Getter
    private FluidTank fluidTank;

    @Getter
    @Setter
    private float heat, maxHeat;

    @Getter
    @Setter
    private boolean  completed;
    private BlockPos lastPos;

    private final float heatPerOperationTick = 30;
    private int tickBeforeHarvest;

    private final NonNullList<ItemStack> tempVarious;
    private       ItemStack              tempSludge;

    public TileSmallMiningDrill()
    {
        super("smallminingdrill", 0);

        this.fluidTank = new FilteredFluidTank(32000,
                fluid -> fluid != null && fluid.getFluid() == FluidRegistry.WATER);
        this.steamTank = new SteamTank(0, steamMachine.getSteamCapacity(),
                steamMachine.getMaxPressureCapacity());

        this.heat = 0;
        this.maxHeat = 3000;
        this.lastPos = this.getPos();

        this.tempVarious = NonNullList.create();
        this.tempSludge = ItemStack.EMPTY;
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        boolean isDirty = false;

        if (!this.isCompleted() && this.tempVarious.isEmpty() && this.heat < this.maxHeat
                && this.getSteamTank().getSteam() >= steamMachine.getSteamConsumption())
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
                this.tickBeforeHarvest = (int) Math.ceil(4 * (1 / (this.getSteamTank().getPressure()
                        / steamMachine.getMaxPressureCapacity())));

                IBlockState state = this.world.getBlockState(toCheck);

                if (!this.world.isAirBlock(toCheck) && !(state.getBlock() instanceof IFluidBlock)
                        && state.getBlockHardness(world, toCheck) >= 0)
                {
                    if (state.getBlock() instanceof BlockVeinOre)
                    {
                        SludgeData sludge = ((BlockVeinOre) state.getBlock()).getOreFromState(state).toSludge();

                        ItemStack sludgeStack = new ItemStack(QBarItems.MINERAL_SLUDGE);
                        sludgeStack.setTagCompound(new NBTTagCompound());
                        sludgeStack.getTagCompound().setTag("sludgeData", sludge.writeToNBT(new NBTTagCompound()));
                        tempSludge = sludgeStack;
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

            this.heat += 30 * (this.getSteamTank().getPressure() / 2);

            this.steamTank.drainSteam((int) Math.max(
                    steamMachine.getSteamConsumption() * this.getSteamTank().getPressure(),
                    steamMachine.getSteamConsumption()), true);
            isDirty = true;
        }
        if (!this.isCompleted())
        {
            if (this.getFluidTank().getFluidAmount() > 0)
            {
                int removable = Math.min(20, this.getFluidTank().getFluidAmount());

                if (this.heat - removable <= this.getMinimumTemp())
                    removable = (int) (this.heat - this.getMinimumTemp());

                if (removable > 0)
                {
                    this.heat = this.heat - removable;
                    this.getFluidTank().drainInternal(removable, true);
                }
            }
        }
        if (!this.tempVarious.isEmpty())
        {
            if (this.tryInsertTrash(this.getFacing()))
                isDirty = true;
        }
        if (!this.tempSludge.isEmpty())
        {
            if (this.tryInsertSludge(this.getFacing()))
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
        return (int) (this.world.getBiome(this.getPos()).getFloatTemperature(this.pos) * 200);
    }

    private boolean tryInsertSludge(final EnumFacing facing)
    {
        TileEntity slugdeTile = this.world.getTileEntity(this.pos.offset(facing, 2));
        if (slugdeTile instanceof IBelt)
        {
            final IBelt sludgeBelt = (IBelt) slugdeTile;

            ItemStack next = this.tempSludge;
            if (sludgeBelt.insert(next, false))
            {
                sludgeBelt.insert(next, true);
                this.tempSludge = ItemStack.EMPTY;
                return true;
            }
        }
        return false;
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

        tag.setTag("steamTank", this.steamTank.writeToNBT(new NBTTagCompound()));
        tag.setTag("fluidTank", this.fluidTank.writeToNBT(new NBTTagCompound()));

        tag.setFloat("heat", this.heat);
        tag.setFloat("maxHeat", this.maxHeat);

        tag.setBoolean("completed", this.isCompleted());
        tag.setLong("lastPos", this.lastPos.toLong());
        tag.setInteger("tickBeforeHarvest", this.tickBeforeHarvest);

        tag.setTag("tempVarious", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.tempVarious));
        tag.setTag("tempSludge", this.tempSludge.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    private ArrayList<MultiblockSide> tmpConnections = new ArrayList<>();

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));
        if (tag.hasKey("fluidTank"))
            this.fluidTank.readFromNBT(tag.getCompoundTag("fluidTank"));

        this.heat = tag.getFloat("heat");
        this.maxHeat = tag.getFloat("maxHeat");

        this.completed = tag.getBoolean("completed");
        this.lastPos = BlockPos.fromLong(tag.getLong("lastPos"));
        this.tickBeforeHarvest = tag.getInteger("tickBeforeHarvest");

        ItemStackHelper.loadAllItems(tag.getCompoundTag("tempVarious"), this.tempVarious);
        this.tempSludge = new ItemStack(tag.getCompoundTag("tempSludge"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.SMALL_MINING_DRILL.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.EAST
                && side.getPos().getX() == 1 && side.getPos().getY() == 0 && side.getPos().getZ() == -1)
        {
            return true;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.WEST
                && side.getPos().getX() == -1 && side.getPos().getY() == 0 && side.getPos().getZ() == -1)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.SMALL_MINING_DRILL.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.EAST
                && side.getPos().getX() == 1 && side.getPos().getY() == 0 && side.getPos().getZ() == -1)
        {
            return (T) this.steamTank;
        }
        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.WEST
                && side.getPos().getX() == -1 && side.getPos().getY() == 0 && side.getPos().getZ() == -1)
        {
            return (T) this.fluidTank;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("smallminingdrill", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).syncFluidValue(this.getFluidTank()::getFluid, this.getFluidTank()::setFluid)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam).addInventory().create();
    }

    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;
        if (FluidUtils.drainPlayerHand(this.getFluidTank(), player)
                || FluidUtils.fillPlayerHand(this.getFluidTank(), player))
        {
            this.markDirty();
            return true;
        }

        player.openGui(QBar.instance, EGui.SMALLMININGDRILL.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
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
