package net.qbar.common.tile;

import lombok.Getter;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.MultiblockComponent;

import java.util.EnumMap;

public abstract class TileMultiblockInventoryBase extends TileInventoryBase
        implements ITileMultiblockCore, ISidedInventory, IContainerProvider
{
    @Getter
    private final MachineDescriptor   descriptor;
    @Getter
    private final MultiblockComponent multiblock;

    private final EnumMap<EnumFacing, SidedInvWrapper> inventoryWrapperCache;

    public TileMultiblockInventoryBase(MachineDescriptor descriptor, int inventorySize)
    {
        super(descriptor.getName(), inventorySize);

        this.descriptor = descriptor;
        this.multiblock = descriptor.get(MultiblockComponent.class);

        this.inventoryWrapperCache = new EnumMap<>(EnumFacing.class);
    }

    protected SidedInvWrapper getInventoryWrapper(EnumFacing side)
    {
        if (!this.inventoryWrapperCache.containsKey(side))
            this.inventoryWrapperCache.put(side, new SidedInvWrapper(this, side));
        return this.inventoryWrapperCache.get(side);
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
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
