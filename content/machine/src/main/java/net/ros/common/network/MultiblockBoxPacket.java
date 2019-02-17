package net.ros.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.ros.common.init.ROSItems;
import net.ros.common.util.ItemUtils;
import net.voxelindustry.steamlayer.network.packet.Message;

public class MultiblockBoxPacket extends Message
{
    private int slotID;

    public MultiblockBoxPacket(int slotID)
    {
        this.slotID = slotID;
    }

    public MultiblockBoxPacket()
    {
    }

    @Override
    public void read(ByteBuf buf)
    {
        this.slotID = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeInt(this.slotID);
    }

    @Override
    public void handle(EntityPlayer sender)
    {
        if (sender.inventoryContainer != null)
        {
            ItemStack stack = sender.openContainer.getSlot(slotID).getStack();

            if (stack.getItem() == ROSItems.MULTIBLOCK_BOX && stack.hasTagCompound())
            {
                NonNullList<ItemStack> items = NonNullList
                        .withSize(stack.getTagCompound().getTagList("Items", 10).tagCount(), ItemStack.EMPTY);
                ItemUtils.loadAllItems(stack.getTagCompound(), items);
                for (ItemStack item : items)
                {
                    if (!sender.addItemStackToInventory(item))
                        InventoryHelper.spawnItemStack(sender.world, sender.posX, sender.posY, sender.posZ, item);
                }
                stack.setCount(0);
            }
        }
    }
}
