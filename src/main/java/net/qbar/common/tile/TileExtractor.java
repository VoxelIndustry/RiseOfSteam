package net.qbar.common.tile;

import java.util.List;

import mezz.jei.ItemFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.qbar.common.block.BlockExtractor;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.grid.IBelt;
import net.qbar.common.grid.IBeltInput;
import net.qbar.common.init.QBarItems;

public class TileExtractor extends TileInventoryBase
        implements ITileInfoProvider, IContainerProvider, IBeltInput, ITickable
{
    private EnumFacing    facing;

    private final boolean hasFilter;

    private ItemFilter    filter;

    public TileExtractor(final boolean hasFilter)
    {
        super("itemextractor", 1);

        this.facing = EnumFacing.UP;

        this.hasFilter = hasFilter;
    }

    public TileExtractor()
    {
        this(false);
    }

    @Override
    public void update()
    {
        if (this.hasItemHandler() && this.hasBelt())
        {
            final IItemHandler itemHandler = this.getItemHandler();

            final int slots = itemHandler.getSlots();
            int currentSlot;
            ItemStack simulated = ItemStack.EMPTY;
            for (currentSlot = 0; currentSlot < slots; currentSlot++)
            {
                simulated = itemHandler.extractItem(currentSlot, 1, true);
                if (!simulated.isEmpty())
                    break;
            }
            if (!simulated.isEmpty() && this.canInsert(simulated))
                this.insert(itemHandler.extractItem(currentSlot, 1, false));
        }
    }

    private void insert(final ItemStack stack)
    {
        ((IBelt) this.world.getTileEntity(this.getPos().down())).insert(stack, true);
    }

    private boolean canInsert(final ItemStack stack)
    {
        final IBelt belt = (IBelt) this.world.getTileEntity(this.getPos().down());

        return belt.insert(stack, false);
    }

    private boolean hasBelt()
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().down());

        return tile != null && tile instanceof IBelt;
    }

    private IItemHandler getItemHandler()
    {
        return this.world.getTileEntity(this.getPos().offset(this.getFacing().getOpposite()))
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFacing());
    }

    private boolean hasItemHandler()
    {
        final TileEntity tile = this.world.getTileEntity(this.getPos().offset(this.getFacing().getOpposite()));

        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.getFacing());
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Orientation: " + this.getFacing());

        if (this.world.getBlockState(this.getPos()).getValue(BlockExtractor.FILTER))
            lines.add("Filter: ");
        lines.add("Inventory: " + this.hasItemHandler());
        lines.add("Belt: " + this.hasBelt());
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
