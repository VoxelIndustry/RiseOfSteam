package net.qbar.common.network;

import io.github.elytra.concrete.Message;
import io.github.elytra.concrete.NetworkContext;
import io.github.elytra.concrete.annotation.field.MarshalledAs;
import io.github.elytra.concrete.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;

@ReceivedOn(Side.SERVER)
public class KeypunchPacket extends Message
{
    @MarshalledAs("u8")
    private int type;

    public KeypunchPacket(final NetworkContext ctx)
    {
        super(ctx);
    }

    public KeypunchPacket(final int windowID, final int property, final FluidStack stack)
    {
        this(QBar.network);

    }

    @Override
    protected void handle(final EntityPlayer sender)
    {

    }
}