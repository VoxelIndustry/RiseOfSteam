package net.qbar.common.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.grid.IBelt;
import net.qbar.common.grid.IBeltInput;
import net.qbar.common.init.QBarItems;

public class TileExtractor extends TileInventoryBase implements ITileInfoProvider, IContainerProvider, IBeltInput
{
    private EnumFacing facing;

    public TileExtractor()
    {
        super("itemextractor", 1);

        this.facing = EnumFacing.UP;
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Orientation: " + this.getFacing());
        lines.add("Filter: ");
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        tag.setInteger("facing", this.facing.ordinal());

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];

        super.readFromNBT(tag);
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("itemextractor", player).player(player.inventory).hotbar().inventory()
                .addInventory().tile(this)
                .filterSlot(0, 80, 43, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .addInventory().create();
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    @Override
    public ItemStack[] inputItems()
    {
        return new ItemStack[] { new ItemStack(Items.APPLE, 1) };
    }

    @Override
    public boolean canInput(final IBelt into)
    {
        return into.getFacing() == this.getFacing();
    }
}
