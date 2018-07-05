package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.ros.common.network.NetworkHandler;
import net.ros.common.network.TileSyncRequestPacket;
import org.yggard.hermod.EventDispatcher;
import org.yggard.hermod.IEventEmitter;

public class TileBase extends TileEntity implements ITileInfoProvider, IEventEmitter
{
    private EventDispatcher eventDispatcher;

    @Getter
    private boolean isSyncLocked;
    @Getter
    private boolean isSyncQueued;

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

    public void askServerSync()
    {
        new TileSyncRequestPacket(this.world.provider.getDimension(), this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ()).sendToServer();
    }

    public void forceSync()
    {
        if (this.world != null)
        {
            NetworkHandler.sendTileToRange(this);
            this.isSyncQueued = false;
        }
    }

    public void sync()
    {
        if (this.world != null)
        {
            if (!this.isSyncLocked())
                NetworkHandler.sendTileToRange(this);
            else
                this.isSyncQueued = true;
        }
    }

    public void syncLock()
    {
        this.isSyncLocked = true;
    }

    public void releaseSyncLock(boolean flushSync)
    {
        this.isSyncLocked = false;

        if (flushSync && this.isSyncQueued)
            this.sync();
        this.isSyncQueued = false;
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
    public void addInfo(ITileInfoList list)
    {

    }

    @Override
    public EventDispatcher getEventDispatcher()
    {
        if (this.eventDispatcher == null)
            this.initEventDispatcher();
        return this.eventDispatcher;
    }

    private void initEventDispatcher()
    {
        this.eventDispatcher = new EventDispatcher();
    }
}
