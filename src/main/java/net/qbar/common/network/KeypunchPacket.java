package net.qbar.common.network;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;
import net.qbar.common.tile.TileKeypunch;

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
                && sender.getEntityWorld().getTileEntity(this.pos) != null
                && sender.getEntityWorld().getTileEntity(this.pos) instanceof TileKeypunch)
        {
            final TileKeypunch tile = (TileKeypunch) sender.getEntityWorld().getTileEntity(this.pos);

            switch (this.type)
            {
                case ITEMSTACK:
                    break;
                case LOAD:
                    break;
                case PRINT:
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