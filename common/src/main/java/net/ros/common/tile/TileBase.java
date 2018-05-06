package net.ros.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.ros.common.network.NetworkHandler;
import net.ros.common.network.TileSyncRequestPacket;
import org.yggard.hermod.EventDispatcher;
import org.yggard.hermod.IEventEmitter;

import java.util.List;

public class TileBase extends TileEntity implements ITileInfoProvider, IEventEmitter
{
    private EventDispatcher eventDispatcher;

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

    public void forceSync()
    {
        new TileSyncRequestPacket(this.world.provider.getDimension(), this.getPos().getX(), this.getPos().getY(),
                this.getPos().getZ()).sendToServer();
    }

    public void sync()
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
