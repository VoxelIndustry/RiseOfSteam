package net.qbar.common.grid;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IBelt extends ITileCable<BeltGrid>
{
    boolean isSlope();

    EnumFacing getFacing();

    void connectInput(BlockPos pos);

    boolean insert(ItemStack stack, boolean doInsert);

    Collection<ItemBelt> getItems();

    /**
     * For internal use only! Used to sync belt contents upon grid modification.
     */
    void itemUpdate();

    boolean hasChanged();

    void setChanged(boolean change);
}
