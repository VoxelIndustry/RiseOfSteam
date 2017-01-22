package net.qbar.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.qbar.common.network.NetworkHandler;

public class QBarTileBase extends TileEntity
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

    protected void sync()
    {
        NetworkHandler.sendTileToRange(this);
    }

    public boolean isServer()
    {
        return !this.world.isRemote;
    }

    public boolean isClient()
    {
        return this.world.isRemote;
    }
}
