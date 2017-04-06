package net.qbar.common.tile.machine;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileCraftingMachineBase;

public class TileRollingMill extends TileCraftingMachineBase
{
    private final IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    public TileRollingMill()
    {
        super(QBarMachines.ROLLING_MILL);
    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
        super.update();

        final EnumFacing orientation = this.getFacing();

        BlockPos search = null;
        switch (orientation)
        {
            case NORTH:
                search = this.getPos().south();
                break;
            case SOUTH:
                search = this.getPos().north();
                break;
            case WEST:
                search = this.getPos().east();
                break;
            case EAST:
                search = this.getPos().west();
                break;
            default:
                search = this.getPos();
                break;
        }
        if (!this.isOutputEmpty() && this.hasBelt(orientation, search))
        {
            if (this.canInsert(this.getStackInSlot(this.getDescriptor().getOutputs()[0]), search))
            {
                this.insert(this.inventoryHandler.extractItem(this.getDescriptor().getOutputs()[0], 1, false), search);
                this.sync();
            }
        }
    }

    private void insert(final ItemStack stack, final BlockPos pos)
    {
        ((IBelt) this.world.getTileEntity(pos)).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack, final BlockPos pos)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(pos);

        return belt.insert(stack, false);
    }

    private boolean hasBelt(final EnumFacing facing, final BlockPos pos)
    {
        final TileEntity tile = this.world.getTileEntity(pos);

        return tile != null && tile instanceof IBelt;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.ROLLING_MILL.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.SOUTH && side.getPos().getX() == 0 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return true;
            else if (side.getFacing() == EnumFacing.NORTH && side.getPos().equals(BlockPos.ORIGIN))
                return true;
        }
        else if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.WEST && side.getPos().getX() == -1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 0)
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = Multiblocks.ROLLING_MILL.worldSideToMultiblockSide(new MultiblockSide(from, facing),
                this.getFacing());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.SOUTH && side.getPos().getX() == 0 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 1)
                return (T) this.inventoryHandler;
            else if (side.getFacing() == EnumFacing.NORTH && side.getPos().equals(BlockPos.ORIGIN))
                return (T) this.inventoryHandler;
        }
        else if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (side.getFacing() == EnumFacing.WEST && side.getPos().getX() == -1 && side.getPos().getY() == 0
                    && side.getPos().getZ() == 0)
                return (T) this.getSteamTank();
        }
        return null;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("rollingmill", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.ROLLINGMILL_UID, 0, 47, 36,
                        slot -> this.isBufferEmpty() && this.isOutputEmpty())
                .outputSlot(1, 116, 35).displaySlot(2, -1000, 0)
                .syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;

        player.openGui(QBar.instance, EGui.ROLLINGMILL.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
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

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return ((BlockMultiblockBase) this.getBlockType()).getDescriptor().getBox(this.getFacing()).offset(this.pos);
    }
}
