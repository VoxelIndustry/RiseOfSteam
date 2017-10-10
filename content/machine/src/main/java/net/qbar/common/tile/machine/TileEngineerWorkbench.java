package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.QBarTileBase;

import javax.annotation.Nullable;

public class TileEngineerWorkbench extends QBarTileBase implements IContainerProvider, ITileMultiblockCore
{
    public TileEngineerWorkbench()
    {

    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.ENGINEERWORKBENCH.getUniqueID(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
