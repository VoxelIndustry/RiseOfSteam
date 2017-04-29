package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.TileCraftingMachineBase;

import javax.annotation.Nullable;

public class TileSortingMachine extends TileCraftingMachineBase
{

    public TileSortingMachine()
    {
        super(QBarMachines.SORTING_MACHINE);
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
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }
}
