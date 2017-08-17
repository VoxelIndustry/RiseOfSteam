package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;

@ReceivedOn(Side.CLIENT)
public class WrenchPacket extends Message
{
    @MarshalledAs("blockpos")
    private BlockPos   pos;
    private EnumFacing facing;

    public WrenchPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public WrenchPacket(BlockPos pos, EnumFacing facing)
    {
        this(QBar.network);

        this.pos = pos;
        this.facing = facing;
    }

    @Override
    protected void handle(EntityPlayer sender)
    {
        for (int i = 0; i < 3; i++)
            sender.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT,
                    pos.getX() + 0.5 + (facing.getFrontOffsetX() / 2.0),
                    pos.getY() + 0.5 + (facing.getFrontOffsetY() / 2.0),
                    pos.getZ() + 0.5 + (facing.getFrontOffsetZ() / 2.0),
                    facing.getFrontOffsetX() * 0.15f + (sender.getEntityWorld().rand.nextFloat() / 8.0),
                    facing.getFrontOffsetY() * 0.15f,
                    facing.getFrontOffsetZ() * 0.15f + (sender.getEntityWorld().rand.nextFloat() / 8.0));
        if (sender.getEntityWorld().rand.nextFloat() > 0.75f)
            sender.getEntityWorld().playSound(sender, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.06f,
                    sender.world.rand.nextFloat() * 0.1F);
    }
}
