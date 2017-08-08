package net.qbar.common.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Collection;

public interface IBelt extends ITileCable<BeltGrid>
{
    boolean isSlope();

    EnumFacing getFacing();

    boolean insert(ItemStack stack, boolean doInsert);

    Collection<ItemBelt> getItems();

    /**
     * For internal use only! Used to sync belt contents upon grid modification.
     */
    void itemUpdate();

    boolean hasChanged();

    void setChanged(boolean change);

    boolean isWorking();

    void setWorking(boolean working);

    long getLastWorkStateChange();
}
