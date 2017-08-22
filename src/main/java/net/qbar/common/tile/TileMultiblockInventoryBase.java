package net.qbar.common.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.multiblock.ITileMultiblockCore;

public abstract class TileMultiblockInventoryBase extends TileInventoryBase implements ITileMultiblockCore,
        ISidedInventory, IContainerProvider
{
    protected final IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    public TileMultiblockInventoryBase(String name, int size)
    {
        super(name, size);
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }
}
