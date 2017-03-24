package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.EGui;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.tile.TileMachineBase;

public class TileRollingMill extends TileMachineBase
{
    public TileRollingMill()
    {
        super("rollingmill", QBarRecipeHandler.ROLLINGMILL_UID, 1, 3, 1, 1);
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
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("rollingmill", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).recipeSlot(0, QBarRecipeHandler.ROLLINGMILL_UID, 0, 47, 36)
                .outputSlot(1, 116, 35).syncIntegerValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncIntegerValue(this::getMaxProgress, this::setMaxProgress).addInventory().create();
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
