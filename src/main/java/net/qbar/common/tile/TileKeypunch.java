package net.qbar.common.tile;

import fr.ourten.teabeans.value.BaseListProperty;
import fr.ourten.teabeans.value.BaseProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.EmptyContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.init.QBarItems;

public class TileKeypunch extends TileInventoryBase implements IContainerProvider, ISidedInventory
{
    private final int[]                       INPUT   = new int[] { 0 };
    private final int[]                       OUTPUT  = new int[] { 1 };

    private final BaseListProperty<ItemStack> craftStacks;
    private final BaseListProperty<ItemStack> filterStacks;

    private final BaseProperty<Boolean>       isCraftTabProperty, canPrintProperty;

    private final InventoryCrafting           fakeInv = new InventoryCrafting(new EmptyContainer(), 3, 3);

    public TileKeypunch()
    {
        super("keypunch", 2);

        this.isCraftTabProperty = new BaseProperty<>(true, "isCraftTabProperty");
        this.canPrintProperty = new BaseProperty<>(false, "canPrintProperty");
        this.craftStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);
        this.filterStacks = new BaseListProperty<>(() -> NonNullList.withSize(9, ItemStack.EMPTY), null);

        this.isCraftTabProperty.addListener(obs ->
        {
            if (this.isCraftTabProperty.getValue())
                this.canPrintProperty.setValue(!this.getRecipeResult().isEmpty());
            else
                this.canPrintProperty.setValue(true);
        });

        this.craftStacks.addListener(obs ->
        {
            if (this.isCraftTabProperty.getValue())
                this.canPrintProperty.setValue(!this.getRecipeResult().isEmpty());
        });
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
                .syncBooleanValue(this.isCraftTabProperty::getValue, this.isCraftTabProperty::setValue)
                .syncBooleanValue(this.getCanPrintProperty()::getValue, this.getCanPrintProperty()::setValue)
                .syncItemValue(() -> this.getCraftStacks().get(0), stack -> this.getCraftStacks().set(0, stack))
                .syncItemValue(() -> this.getCraftStacks().get(1), stack -> this.getCraftStacks().set(1, stack))
                .syncItemValue(() -> this.getCraftStacks().get(2), stack -> this.getCraftStacks().set(2, stack))
                .syncItemValue(() -> this.getCraftStacks().get(3), stack -> this.getCraftStacks().set(3, stack))
                .syncItemValue(() -> this.getCraftStacks().get(4), stack -> this.getCraftStacks().set(4, stack))
                .syncItemValue(() -> this.getCraftStacks().get(5), stack -> this.getCraftStacks().set(5, stack))
                .syncItemValue(() -> this.getCraftStacks().get(6), stack -> this.getCraftStacks().set(6, stack))
                .syncItemValue(() -> this.getCraftStacks().get(7), stack -> this.getCraftStacks().set(7, stack))
                .syncItemValue(() -> this.getCraftStacks().get(8), stack -> this.getCraftStacks().set(8, stack))
                .syncItemValue(() -> this.getFilterStacks().get(0), stack -> this.getFilterStacks().set(0, stack))
                .syncItemValue(() -> this.getFilterStacks().get(1), stack -> this.getFilterStacks().set(1, stack))
                .syncItemValue(() -> this.getFilterStacks().get(2), stack -> this.getFilterStacks().set(2, stack))
                .syncItemValue(() -> this.getFilterStacks().get(3), stack -> this.getFilterStacks().set(3, stack))
                .syncItemValue(() -> this.getFilterStacks().get(4), stack -> this.getFilterStacks().set(4, stack))
                .syncItemValue(() -> this.getFilterStacks().get(5), stack -> this.getFilterStacks().set(5, stack))
                .syncItemValue(() -> this.getFilterStacks().get(6), stack -> this.getFilterStacks().set(6, stack))
                .syncItemValue(() -> this.getFilterStacks().get(7), stack -> this.getFilterStacks().set(7, stack))
                .syncItemValue(() -> this.getFilterStacks().get(8), stack -> this.getFilterStacks().set(8, stack))
                .addInventory().create();
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

    public BaseProperty<Boolean> getCanPrintProperty()
    {
        return this.canPrintProperty;
    }

    public BaseListProperty<ItemStack> getCraftStacks()
    {
        return this.craftStacks;
    }

    public BaseListProperty<ItemStack> getFilterStacks()
    {
        return this.filterStacks;
    }

    public ItemStack getRecipeResult()
    {
        for (int i = 0; i < 9; i++)
            this.fakeInv.setInventorySlotContents(i, this.getCraftStacks().get(i));
        return CraftingManager.getInstance().findMatchingRecipe(this.fakeInv, this.getWorld());
    }
}
