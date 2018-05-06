package net.ros.common.grid.node;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.ros.common.grid.ItemBelt;
import net.ros.common.grid.impl.BeltGrid;

public interface IBelt extends ITileCable<BeltGrid>
{
    boolean isSlope();

    EnumFacing getFacing();

    default boolean insert(ItemStack stack, boolean doInsert)
    {
        return this.insert(stack, 10 / 32F, 0, doInsert);
    }

    boolean insert(ItemStack stack, float posX, float posY, boolean doInsert);

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
