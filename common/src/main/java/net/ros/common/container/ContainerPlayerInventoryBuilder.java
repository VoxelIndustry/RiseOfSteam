package net.ros.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.ros.common.container.slot.FilteredSlot;
import net.ros.common.container.slot.ListenerSlot;
import org.apache.commons.lang3.Range;

public final class ContainerPlayerInventoryBuilder
{
    private final EntityPlayer     player;
    private final PlayerInvWrapper inventory;
    private final ContainerBuilder parent;
    private       Range<Integer>   main;
    private       Range<Integer>   hotbar;
    private       Range<Integer>   armor;

    ContainerPlayerInventoryBuilder(final ContainerBuilder parent, EntityPlayer player, PlayerInvWrapper inventory)
    {
        this.inventory = inventory;
        this.player = player;
        this.parent = parent;
    }

    /**
     * Utility method to add the entire main inventory of a inventory to the slot list.
     * Note that this does not include the hotbar nor the armor slots.
     *
     * @param xStart the horizontal position at which the inventory begins
     * @param yStart the vertical position at which the inventory begins
     * @return a reference to this {@code ContainerPlayerInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerPlayerInventoryBuilder inventory(final int xStart, final int yStart)
    {
        final int startIndex = this.parent.slots.size();
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.parent.slots.add(new ListenerSlot(this.inventory, j + i * 9 + 9, xStart + j * 18, yStart + i *
                        18));
        this.main = Range.between(startIndex, this.parent.slots.size() - 1);
        return this;
    }

    /**
     * Utility method to add the entire hotbar of a inventory to the slot list.
     *
     * @param xStart the horizontal position at which the inventory begins
     * @param yStart the vertical position at which the inventory begins
     * @return a reference to this {@code ContainerPlayerInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerPlayerInventoryBuilder hotbar(final int xStart, final int yStart)
    {
        final int startIndex = this.parent.slots.size();
        for (int i = 0; i < 9; ++i)
            this.parent.slots.add(new ListenerSlot(this.inventory, i, xStart + i * 18, yStart));
        this.hotbar = Range.between(startIndex, this.parent.slots.size() - 1);
        return this;
    }

    /**
     * Utility method to add the entire main inventory of a inventory to the slot list.
     * Note that this does not include the hotbar nor the armor slots.
     * <p>
     * This method will use commonly used default values to position the slots.
     *
     * @return a reference to this {@code ContainerPlayerInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerPlayerInventoryBuilder inventory()
    {
        return this.inventory(8, 94);
    }

    /**
     * Utility method to add the entire hotbar of a inventory to the slot list.
     * <p>
     * This method will use commonly used default values to position the slots.
     *
     * @return a reference to this {@code ContainerPlayerInventoryBuilder} to resume the "Builder" pattern
     */
    public ContainerPlayerInventoryBuilder hotbar()
    {
        return this.hotbar(8, 152);
    }

    /**
     * Begin the construction of a {@link ContainerPlayerArmorInventoryBuilder} builder.
     * Only one should be used per {@code ContainerPlayerInventoryBuilder}
     *
     * @return a {@link ContainerPlayerArmorInventoryBuilder} marked as child of this builder
     */
    public ContainerPlayerArmorInventoryBuilder armor()
    {
        return new ContainerPlayerArmorInventoryBuilder(this);
    }

    /**
     * Close this builder and add the slot list to the current {@link BuiltContainer} construction.
     * <p>
     * A special case has been implemented with armor slots, they are considered as a tile slot range. Allowing
     * shift-insert from a inventory inventory.
     *
     * @return the parent {@link ContainerBuilder} to resume the "Builder" pattern
     */
    public ContainerBuilder addInventory()
    {
        if (this.hotbar != null)
            this.parent.addPlayerInventoryRange(this.hotbar);
        if (this.main != null)
            this.parent.addPlayerInventoryRange(this.main);
        if (this.armor != null)
            this.parent.addTileInventoryRange(this.armor);

        return this.parent;
    }

    public static final class ContainerPlayerArmorInventoryBuilder
    {
        private final ContainerPlayerInventoryBuilder parent;
        private final int                             startIndex;

        public ContainerPlayerArmorInventoryBuilder(final ContainerPlayerInventoryBuilder parent)
        {
            this.parent = parent;
            this.startIndex = parent.parent.slots.size();
        }

        private ContainerPlayerArmorInventoryBuilder armor(final int index, final int xStart, final int yStart,
                                                           final EntityEquipmentSlot slotType)
        {

            this.parent.parent.slots.add(new FilteredSlot(this.parent.inventory, index, xStart, yStart)
                    .setFilter(stack -> stack.getItem().isValidArmor(stack, slotType, parent.player)));
            return this;
        }

        public ContainerPlayerArmorInventoryBuilder helmet(final int xStart, final int yStart)
        {
            return this.armor(this.parent.inventory.getSlots() - 2, xStart, yStart, EntityEquipmentSlot.HEAD);
        }

        public ContainerPlayerArmorInventoryBuilder chestplate(final int xStart, final int yStart)
        {
            return this.armor(this.parent.inventory.getSlots() - 3, xStart, yStart, EntityEquipmentSlot.CHEST);
        }

        public ContainerPlayerArmorInventoryBuilder leggings(final int xStart, final int yStart)
        {
            return this.armor(this.parent.inventory.getSlots() - 4, xStart, yStart, EntityEquipmentSlot.LEGS);
        }

        public ContainerPlayerArmorInventoryBuilder boots(final int xStart, final int yStart)
        {
            return this.armor(this.parent.inventory.getSlots() - 5, xStart, yStart, EntityEquipmentSlot.FEET);
        }

        public ContainerPlayerArmorInventoryBuilder complete(final int xStart, final int yStart)
        {
            return this.helmet(xStart, yStart).chestplate(xStart, yStart + 18).leggings(xStart, yStart + 18 + 18)
                    .boots(xStart, yStart + 18 + 18 + 18);
        }

        public ContainerPlayerInventoryBuilder addArmor()
        {
            this.parent.armor = Range.between(this.startIndex, this.parent.parent.slots.size() - 1);
            return this.parent;
        }
    }
}