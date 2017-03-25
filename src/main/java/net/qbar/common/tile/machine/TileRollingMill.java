package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileCraftingMachineBase;

public class TileRollingMill extends TileCraftingMachineBase
{
    public TileRollingMill()
    {
        super(QBarMachines.ROLLING_MILL);
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
        final EnumFacing orientation = this.getFacing();
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (orientation == EnumFacing.EAST && from.getX() == 0 && from.getY() == 0 && from.getZ() == 1
                    && facing == EnumFacing.SOUTH)
                return true;
            if (orientation == EnumFacing.WEST && from.getX() == 0 && from.getY() == 0 && from.getZ() == -1
                    && facing == EnumFacing.NORTH)
                return true;
            if (orientation == EnumFacing.SOUTH && from.getX() == -1 && from.getY() == 0 && from.getZ() == 0
                    && facing == EnumFacing.WEST)
                return true;
            if (orientation == EnumFacing.NORTH && from.getX() == 1 && from.getY() == 0 && from.getZ() == 0
                    && facing == EnumFacing.EAST)
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        final EnumFacing orientation = this.getFacing();
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
        {
            if (orientation == EnumFacing.EAST && from.getX() == 0 && from.getY() == 0 && from.getZ() == 1
                    && facing == EnumFacing.SOUTH)
                return (T) this.getSteamTank();
            if (orientation == EnumFacing.WEST && from.getX() == 0 && from.getY() == 0 && from.getZ() == -1
                    && facing == EnumFacing.NORTH)
                return (T) this.getSteamTank();
            if (orientation == EnumFacing.SOUTH && from.getX() == -1 && from.getY() == 0 && from.getZ() == 0
                    && facing == EnumFacing.WEST)
                return (T) this.getSteamTank();
            if (orientation == EnumFacing.NORTH && from.getX() == 1 && from.getY() == 0 && from.getZ() == 0
                    && facing == EnumFacing.EAST)
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
                .addInventory().tile(this).recipeSlot(0, QBarRecipeHandler.ROLLINGMILL_UID, 0, 47, 36)
                .outputSlot(1, 116, 35).syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(QBar.instance, EGui.ROLLINGMILL.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
