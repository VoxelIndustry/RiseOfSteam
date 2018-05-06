package net.ros.common.network.action;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.network.ServerActionHolderPacket;

public class ServerActionBuilder
{
    private String actionKey;

    private TileEntity               tile;
    private NBTTagCompound           payload;
    private ServerActionHolderPacket packet;


    public ServerActionBuilder(String actionKey)
    {
        this.actionKey = actionKey;
        this.payload = new NBTTagCompound();
    }

    public ServerActionBuilder toTile(TileEntity tile)
    {
        this.tile = tile;
        this.packet = new ServerActionHolderPacket(this.tile, this.actionKey);
        return this;
    }

    public ServerActionBuilder withInt(String key, Integer value)
    {
        this.payload.setInteger(key, value);
        return this;
    }

    public ServerActionBuilder withString(String key, String value)
    {
        this.payload.setString(key, value);
        return this;
    }

    public ServerActionBuilder withFloat(String key, Float value)
    {
        this.payload.setFloat(key, value);
        return this;
    }

    public ServerActionBuilder withDouble(String key, Double value)
    {
        this.payload.setDouble(key, value);
        return this;
    }

    public ServerActionBuilder withBoolean(String key, Boolean value)
    {
        this.payload.setBoolean(key, value);
        return this;
    }

    public ServerActionBuilder withItemStack(String key, ItemStack value)
    {
        this.payload.setTag(key, value.writeToNBT(new NBTTagCompound()));
        return this;
    }

    public ServerActionBuilder withFluidStack(String key, FluidStack value)
    {
        this.payload.setTag(key, value.writeToNBT(new NBTTagCompound()));
        return this;
    }

    public ServerActionBuilder then(IActionCallback callback)
    {
        ActionManager.getInstance().addCallback(this.packet.getActionID(), callback);
        this.packet.setExpectAnswer(true);
        return this;
    }

    public void send()
    {
        this.packet.setActionPayload(payload);
        this.packet.sendToServer();
    }
}
