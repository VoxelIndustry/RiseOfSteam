package net.qbar.common.tile;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.qbar.common.network.NetworkHandler;
import net.qbar.common.network.TileSyncRequestPacket;

public class QBarTileBase extends TileEntity implements ITileInfoProvider
{
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        final NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTag);
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity packet)
    {
        this.readFromNBT(packet.getNbtCompound());
    }

    protected void forceSync()
    {
        new TileSyncRequestPacket(this.world.provider.getDimension(), this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ()).sendToServer();
    }

    protected void sync()
    {
        if (this.world != null)
            NetworkHandler.sendTileToRange(this);
    }

    public boolean isServer()
    {
        if (this.world != null)
            return !this.world.isRemote;
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public boolean isClient()
    {
        if (this.world != null)
            return this.world.isRemote;
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    @Override
    public void addInfo(final List<String> lines)
    {

    }
}
