package net.qbar.common.grid;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.ILoadable;

public interface ITileWorkshop extends ITileCable<WorkshopGrid>, ILoadable
{
    WorkshopMachine getType();

    default WorkshopGrid createGrid(int nextID)
    {
        return new WorkshopGrid(nextID);
    }

    default void disconnectItself()
    {
        GridManager.getInstance().disconnectCable(this);
    }

    default BlockPos getAdjacentPos(final EnumFacing facing)
    {
        BlockPos rtn = this.getBlockPos().offset(facing);
        while (this.getBlockWorld().getTileEntity(rtn) != null && this.getBlockWorld().getTileEntity(rtn) instanceof
                ITileMultiblock &&
                !(this.getBlockWorld().getTileEntity(rtn) instanceof ITileMultiblockCore))
            rtn = rtn.offset(facing);
        return rtn;
    }

    default void load()
    {
        GridManager.getInstance().connectCable(this);
    }

    @Override
    default boolean canConnect(ITileNode<?> to)
    {
        return to instanceof ITileWorkshop && ((ITileWorkshop) to).getType() != this.getType() &&
                (!this.hasGrid() || !this.getGridObject().getMachines().containsKey(((ITileWorkshop) to).getType()) ||
                        this.getGridObject().getMachines().get(((ITileWorkshop) to).getType()).equals(to));
    }
}
