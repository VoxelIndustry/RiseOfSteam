package net.qbar.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.init.QBarItems;

public class TileKeypunch extends TileInventoryBase implements IContainerProvider, ISidedInventory
{
    private final int[] INPUT  = new int[] { 0 };
    private final int[] OUTPUT = new int[] { 1 };

    public TileKeypunch()
    {
        super("keypunch", 2);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("keypunch", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .filterSlot(0, 26, 61, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .outputSlot(1, 134, 61).addInventory().create();
    }

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return side.equals(EnumFacing.DOWN) ? this.OUTPUT : this.INPUT;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack stack, final EnumFacing side)
    {
        if (!side.equals(EnumFacing.DOWN))
            return this.isItemValidForSlot(index, stack);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing side)
    {
        return side.equals(EnumFacing.DOWN);
    }
}
