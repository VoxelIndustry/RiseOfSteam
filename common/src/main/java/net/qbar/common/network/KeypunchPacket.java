package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.FilterCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.machine.TileKeypunch;

@ReceivedOn(Side.SERVER)
public class KeypunchPacket extends Message
{
    private EKeypunchPacketType type;

    @MarshalledAs("u8")
    private int                 currentTab;

    @MarshalledAs("u8")
    private int                 slot;

    @MarshalledAs("stack")
    private ItemStack           stack;

    @MarshalledAs("blockpos")
    private BlockPos            pos;
    @MarshalledAs("i32")
    private int                 dimensionID;

    public KeypunchPacket(final NetworkContext ctx)
    {
        super(ctx);

        this.stack = ItemStack.EMPTY;
    }

    public KeypunchPacket(final TileKeypunch tile, final int currentTab)
    {
        this(QBar.network);
        this.currentTab = currentTab;

        this.pos = tile.getPos();
        this.dimensionID = tile.getWorld().provider.getDimension();

        this.type = EKeypunchPacketType.TAB;
    }

    public KeypunchPacket(final TileKeypunch tile, final int slot, final ItemStack stack)
    {
        this(QBar.network);
        this.slot = slot;
        this.stack = stack;

        this.pos = tile.getPos();
        this.dimensionID = tile.getWorld().provider.getDimension();

        this.type = EKeypunchPacketType.ITEMSTACK;
    }

    public KeypunchPacket(final TileKeypunch tile, final boolean load)
    {
        this(QBar.network);

        this.pos = tile.getPos();
        this.dimensionID = tile.getWorld().provider.getDimension();

        if (load)
            this.type = EKeypunchPacketType.LOAD;
        else
            this.type = EKeypunchPacketType.PRINT;
    }

    @Override
    protected void handle(final EntityPlayer sender)
    {
        if (sender.getEntityWorld().provider.getDimension() == this.dimensionID
                && sender.getEntityWorld().isBlockLoaded(this.pos)
                && sender.getEntityWorld().getTileEntity(this.pos) != null
                && sender.getEntityWorld().getTileEntity(this.pos) instanceof TileKeypunch)
        {
            final TileKeypunch tile = (TileKeypunch) sender.getEntityWorld().getTileEntity(this.pos);

            switch (this.type)
            {
                case ITEMSTACK:
                    if (tile.getCraftTabProperty().getValue())
                        tile.getCraftStacks().set(this.slot, this.stack);
                    else
                        tile.getFilterStacks().set(this.slot, this.stack);
                    tile.markDirty();
                    break;
                case LOAD:
                    if (tile.getCraftTabProperty().getValue())
                    {
                        if (tile.getStackInSlot(0).hasTagCompound() && tile.getStackInSlot(0).getTagCompound()
                                .getInteger("cardTypeID") == ECardType.CRAFT.getID())
                        {
                            final CraftCard card = (CraftCard) PunchedCardDataManager.getInstance()
                                    .readFromNBT(tile.getStackInSlot(0).getTagCompound());
                            for (int i = 0; i < card.recipe.length; i++)
                                tile.getCraftStacks().set(i, card.recipe[i]);
                            tile.markDirty();

                        }
                    }
                    else
                    {
                        if (tile.getStackInSlot(0).hasTagCompound() && tile.getStackInSlot(0).getTagCompound()
                                .getInteger("cardTypeID") == ECardType.FILTER.getID())
                        {
                            final FilterCard card = (FilterCard) PunchedCardDataManager.getInstance()
                                    .readFromNBT(tile.getStackInSlot(0).getTagCompound());
                            for (int i = 0; i < card.stacks.length; i++)
                                tile.getFilterStacks().set(i, card.stacks[i]);
                            tile.markDirty();
                        }
                    }
                    break;
                case PRINT:
                    if (tile.getCraftTabProperty().getValue())
                    {
                        if (tile.getCanPrintProperty().getValue())
                        {
                            final ItemStack punched = new ItemStack(QBarItems.PUNCHED_CARD, 1, 1);
                            punched.setTagCompound(new NBTTagCompound());
                            final CraftCard card = new CraftCard(ECardType.CRAFT.getID());
                            for (int i = 0; i < tile.getCraftStacks().size(); i++)
                                card.recipe[i] = tile.getCraftStacks().get(i);
                            card.result = tile.getRecipeResult();
                            PunchedCardDataManager.getInstance().writeToNBT(punched.getTagCompound(), card);
                            tile.decrStackSize(0, 1);
                            tile.setInventorySlotContents(1, punched);
                            tile.markDirty();
                        }
                    }
                    else
                    {
                        final ItemStack punched = new ItemStack(QBarItems.PUNCHED_CARD, 1, 1);
                        punched.setTagCompound(new NBTTagCompound());
                        final FilterCard card = new FilterCard(ECardType.FILTER.getID());
                        for (int i = 0; i < tile.getFilterStacks().size(); i++)
                            card.stacks[i] = tile.getFilterStacks().get(i);
                        PunchedCardDataManager.getInstance().writeToNBT(punched.getTagCompound(), card);
                        tile.decrStackSize(0, 1);
                        tile.setInventorySlotContents(1, punched);
                        tile.markDirty();
                    }
                    break;
                case TAB:
                    tile.getCraftTabProperty().setValue(this.currentTab == 0);
                    tile.markDirty();
                    break;
                default:
                    break;
            }
        }
    }

    public static enum EKeypunchPacketType
    {
        TAB, ITEMSTACK, LOAD, PRINT;
    }
}