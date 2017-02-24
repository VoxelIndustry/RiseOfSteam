package net.qbar.common.tile;

import fr.ourten.teabeans.value.BaseListProperty;
import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.init.QBarItems;

public class TileKeypunch extends TileInventoryBase implements IContainerProvider, ISidedInventory
{
    private final int[]                       INPUT  = new int[] { 0 };
    private final int[]                       OUTPUT = new int[] { 1 };

    private final BaseListProperty<ItemStack> craftStacks;
    private final BaseListProperty<ItemStack> filterStacks;

    private final BaseProperty<Boolean>       isCraftTabProperty;

    public TileKeypunch()
    {
        super("keypunch", 2);

        this.isCraftTabProperty = new BaseProperty<>(true, "isCraftTabProperty");
        this.craftStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);
        this.filterStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setBoolean("isCraftTab", this.isCraftTabProperty.getValue());

        final NBTTagCompound craftTag = new NBTTagCompound();
        ItemStackHelper.saveAllItems(craftTag, (NonNullList<ItemStack>) this.craftStacks.getModifiableValue());
        tag.setTag("craftTag", craftTag);

        final NBTTagCompound filterTag = new NBTTagCompound();
        ItemStackHelper.saveAllItems(filterTag, (NonNullList<ItemStack>) this.filterStacks.getModifiableValue());
        tag.setTag("filterTag", filterTag);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.isCraftTabProperty.setValue(tag.getBoolean("isCraftTab"));

        final NonNullList<ItemStack> tmpCraft = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag.getCompoundTag("craftTag"), tmpCraft);

        final NonNullList<ItemStack> tmpFilter = NonNullList.withSize(9, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tag.getCompoundTag("filterTag"), tmpFilter);

        for (int i = 0; i < 9; i++)
        {
            this.craftStacks.set(i, tmpCraft.get(i));
            this.filterStacks.set(i, tmpFilter.get(i));
        }
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("keypunch", player).player(player.inventory).inventory(8, 93).hotbar(8, 151)
                .addInventory().tile(this)
                .filterSlot(0, 26, 70, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .outputSlot(1, 134, 70)
                .syncBooleanValue(this.isCraftTabProperty::getValue, this.isCraftTabProperty::setValue).addInventory()
                .create();
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

    public BaseProperty<Boolean> getCraftTabProperty()
    {
        return this.isCraftTabProperty;
    }

    public BaseListProperty<ItemStack> getCraftStacks()
    {
        return this.craftStacks;
    }

    public BaseListProperty<ItemStack> getFilterStacks()
    {
        return this.filterStacks;
    }
}
