package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.client.fx.SteamParticleHelper;
import net.qbar.common.QBarConstants;

@ReceivedOn(Side.CLIENT)
public class SteamEffectPacket extends Message
{
    @MarshalledAs("i32")
    private int      dimension;
    private BlockPos pos;
    private BlockPos target;
    private boolean  small;

    public SteamEffectPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public SteamEffectPacket(World w, BlockPos pos, BlockPos target, boolean small)
    {
        this(QBarConstants.network);

        this.dimension = w.provider.getDimension();
        this.pos = pos;
        this.target = target;
        this.small = small;
    }

    @Override
    protected void handle(EntityPlayer player)
    {
        if (player.getEntityWorld().provider.getDimension() != dimension)
            return;

        World world = player.getEntityWorld();

        if (world.isBlockLoaded(this.pos))
            SteamParticleHelper.createSmallSteamJet(world, pos, target, small);
    }
}
