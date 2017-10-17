package net.qbar.common.network.action;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.qbar.common.network.ClientActionHolderPacket;

public class ClientActionBuilder
{
    private TileEntity sender;
    private EntityPlayer     player;
    private NBTTagCompound payload;
    private short replyID;

    public ClientActionBuilder(short replyID, TileEntity sender)
    {
        this.sender = sender;
        this.payload = new NBTTagCompound();
        this.replyID = replyID;
    }

    public ClientActionBuilder toPlayer(EntityPlayer player)
    {
        this.player = player;
        return this;
    }

    public ClientActionBuilder withInt(String key, Integer value)
    {
        this.payload.setInteger(key, value);
        return this;
    }

    public ClientActionBuilder withString(String key, String value)
    {
        this.payload.setString(key, value);
        return this;
    }

    public ClientActionBuilder withFloat(String key, Float value)
    {
        this.payload.setFloat(key, value);
        return this;
    }

    public ClientActionBuilder withDouble(String key, Double value)
    {
        this.payload.setDouble(key, value);
        return this;
    }

    public ClientActionBuilder withBoolean(String key, Boolean value)
    {
        this.payload.setBoolean(key, value);
        return this;
    }

    public ClientActionBuilder withItemStack(String key, ItemStack value)
    {
        this.payload.setTag(key, value.writeToNBT(new NBTTagCompound()));
        return this;
    }

    public ClientActionBuilder withFluidStack(String key, FluidStack value)
    {
        this.payload.setTag(key, value.writeToNBT(new NBTTagCompound()));
        return this;
    }

    public void send()
    {
        new ClientActionHolderPacket(this.replyID, this.sender, this.payload).sendTo(this.player);
    }
}
