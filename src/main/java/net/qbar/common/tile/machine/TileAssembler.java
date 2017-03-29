package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.QBar;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileInventoryBase;
import net.qbar.common.util.ItemUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class TileAssembler extends TileInventoryBase
        implements IContainerProvider, ITickable, ITileMultiblockCore, ISidedInventory
{
    private final SteamTank steamTank;

    private CraftCard       craft;
    private ItemStack       cached = ItemStack.EMPTY;

    private EnumFacing      facing;

    // 0 : PunchedCard
    // 1 - 9 : Crafting Ingredients
    // 10 : Result
    // 11 - 19 : Remaining Ingredients
    public TileAssembler()
    {
        super("assembler", 20);

        this.steamTank = new SteamTank(0, 4000, SteamUtil.AMBIANT_PRESSURE * 2);
        this.facing = EnumFacing.NORTH;
    }

    @Override
    public void update()
    {
        if (!ItemUtils.deepEquals(this.cached, this.getStackInSlot(0)))
        {
            this.craft = null;
            this.cached = this.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = PunchedCardDataManager.getInstance()
                        .readFromNBT(this.getStackInSlot(0).getTagCompound());
                if (card.getID() == ECardType.CRAFT.getID())
                    this.craft = (CraftCard) card;
            }
        }

        if (this.steamTank.getSteam() > 0 && this.craft != null)
        {
            if (this.getStackInSlot(10).isEmpty() && this.checkIngredients())
            {
                this.produce();
            }
        }
    }

    private boolean checkIngredients()
    {
        return true;
    }

    private void produce()
    {
        this.setInventorySlotContents(10, this.craft.result.copy());
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        tag.setInteger("facing", this.facing.ordinal());

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));
        this.facing = EnumFacing.VALUES[tag.getInteger("facing")];
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }

    IItemHandler inventoryHandler = new SidedInvWrapper(this, EnumFacing.NORTH);

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing == EnumFacing.UP)
            return true;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return (T) this.steamTank;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == this.getFacing())
            return (T) this.inventoryHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("assembler", player).player(player.inventory).inventory(8, 103).hotbar(8, 161)
                .addInventory().tile(this)
                .filterSlot(0, 26, 17, stack -> !stack.isEmpty() && stack.getItem().equals(QBarItems.PUNCHED_CARD))
                .slot(1, 8, 61).slot(2, 26, 61).slot(3, 44, 61).slot(4, 62, 61).slot(5, 80, 61).slot(6, 8, 79).slot(7, 26, 79).slot(8, 44, 79).slot(9, 62, 79)
                .outputSlot(10, 144, 19).outputSlot(11, 98, 61).outputSlot(12, 116, 61).outputSlot(13, 134, 61)
                .outputSlot(14, 152, 61).outputSlot(15, 80, 79).outputSlot(16, 98, 79).outputSlot(17, 116, 79)
                .outputSlot(18, 134, 79).outputSlot(19, 155, 79).addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(QBar.instance, EGui.ASSEMBLER.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }

    private final int[] inputSlots   = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private final int[] outputsSlots = new int[] { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        return this.inputSlots;
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.inputSlots, index))
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.outputsSlots, index))
            return true;
        return false;
    }
}