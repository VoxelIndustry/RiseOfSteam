package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.grid.IBelt;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileCraftingMachineBase;
import org.apache.commons.lang3.ArrayUtils;

public class TileSteamFurnaceMK2 extends TileCraftingMachineBase
{
    private final IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    private int currentHeat, maxHeat;

    private ItemStack cachedStack;

    public TileSteamFurnaceMK2()
    {
        super(QBarMachines.FURNACE_MK2);

        this.maxHeat = 2000;
        this.cachedStack = ItemStack.EMPTY;
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
        super.update();

        final EnumFacing orientation = this.getFacing().getOpposite();

        if (!this.isOutputEmpty() && this.hasBelt(orientation))
        {
            if (this.canInsert(this.getStackInSlot(this.getDescriptor().getOutputs()[0]), orientation))
            {
                this.insert(this.inventoryHandler.extractItem(this.getDescriptor().getOutputs()[0], 1, false),
                        orientation);
                this.sync();
            }
        }

        if (this.world.getTotalWorldTime() % 5 == 0 && this.getSteamTank().getSteam() > 0
                && this.currentHeat < this.maxHeat)
        {
            this.getSteamTank().drainInternal(1, true);
            this.currentHeat++;
        }
    }

    @Override
    public void onRecipeChange()
    {
        if (this.getCurrentRecipe() != null)
            this.cachedStack = this.getCurrentRecipe().getRecipeOutputs(ItemStack.class).get(0).getRawIngredient();
    }

    @Override
    public float getEfficiency()
    {
        return (float) this.currentHeat / this.getMaxHeat();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("heat", this.currentHeat);

        tag.setTag("cachedStack", this.cachedStack.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentHeat = tag.getInteger("heat");

        this.cachedStack = new ItemStack(tag.getCompoundTag("cachedStack"));
    }

    private void insert(final ItemStack stack, final EnumFacing facing)
    {
        ((IBelt) this.world.getTileEntity(this.pos.offset(facing, 2).offset(facing.rotateY()))).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack, final EnumFacing facing)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.pos.offset(facing, 2).offset(facing.rotateY()));

        return belt.insert(stack, false);
    }

    private boolean hasBelt(final EnumFacing facing)
    {
        final TileEntity tile = this.world.getTileEntity(this.pos.offset(facing, 2).offset(facing.rotateY()));

        return tile != null && tile instanceof IBelt;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        final EnumFacing orientation = this.getFacing();

        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = Multiblocks.STEAM_FURNACE_MK2
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getPos().equals(BlockPos.ORIGIN) && side.getFacing() == EnumFacing.WEST)
                return true;
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = Multiblocks.STEAM_FURNACE_MK2
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getPos().getX() == 1 && side.getPos().getY() == 1 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = Multiblocks.STEAM_FURNACE_MK2
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getPos().equals(BlockPos.ORIGIN) && side.getFacing() == EnumFacing.WEST)
                return (T) this.getSteamTank();
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            MultiblockSide side = Multiblocks.STEAM_FURNACE_MK2
                    .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());
            if (side.getPos().getX() == 1 && side.getPos().getY() == 1 && side.getPos().getZ() == 1
                    && side.getFacing() == EnumFacing.SOUTH)
                return (T) this.inventoryHandler;
        }
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("furnacemk2", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.FURNACE_UID, 0, 47, 36,
                        slot -> this.isBufferEmpty() && this.isOutputEmpty())
                .outputSlot(1, 116, 35).displaySlot(2, -1000, 0)
                .syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam)
                .syncIntegerValue(this::getCurrentHeat, this::setCurrentHeat)
                .syncIntegerValue(this::getMaxHeat, this::setMaxHeat).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBar.instance, EGui.STEAMFURNACEMK2.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.getDescriptor().getInputs(), index) && this.isInputEmpty() && this.isBufferEmpty()
                && this.isOutputEmpty())
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    public int getCurrentHeat()
    {
        return currentHeat;
    }

    public void setCurrentHeat(int currentHeat)
    {
        this.currentHeat = currentHeat;
    }

    public int getMaxHeat()
    {
        return maxHeat;
    }

    public void setMaxHeat(int maxHeat)
    {
        this.maxHeat = maxHeat;
    }

    public ItemStack getCachedStack()
    {
        return this.cachedStack;
    }
}
