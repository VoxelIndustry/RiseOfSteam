package net.ros.common.tile.creative;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.steam.CreativeSteamTank;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.steam.SteamTank;
import net.ros.common.tile.ITileInfoList;
import net.ros.common.tile.TileBase;

public class TileCreativeSteamGenerator extends TileBase
{
    private final SteamTank steamTank;

    public TileCreativeSteamGenerator()
    {
        this.steamTank = new CreativeSteamTank();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == SteamCapabilities.STEAM_HANDLER)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == SteamCapabilities.STEAM_HANDLER)
            return (T) this.steamTank;
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        list.addText("Steam " + Double.POSITIVE_INFINITY);
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }
}
