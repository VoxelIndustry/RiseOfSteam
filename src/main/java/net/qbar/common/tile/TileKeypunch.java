package net.qbar.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.init.QBarItems;

public class TileKeypunch extends TileInventoryBase implements IContainerProvider
{
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
}
