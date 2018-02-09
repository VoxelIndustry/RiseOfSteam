package net.qbar.common.grid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IBelt extends ITileCable<BeltGrid>
{
    boolean isSlope();

    EnumFacing getFacing();

    boolean insert(ItemStack stack, boolean doInsert);

    ItemBelt[] getItems();

    /**
     * For internal use only! Used to sync belt contents upon grid modification.
     */
    void itemUpdate();

    boolean hasChanged();

    void setChanged(boolean change);

    boolean isWorking();

    void setWorking(boolean working);

    long getLastWorkStateChange();

    default boolean isEmpty()
    {
        return this.getItems()[0] == null && this.getItems()[1] == null && this.getItems()[2] == null;
    }

    default void addItem(ItemBelt item)
    {
        for (int i = 0; i < 3; i++)
        {
            if (this.getItems()[i] == null)
            {
                this.getItems()[i] = item;
                break;
            }
        }
    }
}
