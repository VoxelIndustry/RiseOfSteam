package net.qbar.common.tile.creative;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.CreativeSteamTank;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.tile.TileInventoryBase;

import java.util.List;

public class TileCreativeSteamGenerator extends TileInventoryBase
{
    private final SteamTank steamTank;

    public TileCreativeSteamGenerator()
    {
        super("TileCreativeSteamGenerator", 0);

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
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return (T) this.steamTank;
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + Double.POSITIVE_INFINITY);
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }
}
