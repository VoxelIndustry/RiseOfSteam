package net.qbar.common.card;

import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PunchedCardData
{
    private final short        id;

    private final IPunchedCard data;

    PunchedCardData(int id, IPunchedCard data)
    {
        if (id > Short.MAX_VALUE)
            throw new IllegalArgumentException("id to big!");
        if (id < 0)
            throw new IllegalArgumentException("id to small!");
        Objects.requireNonNull(data);

        this.id = (short) id;
        this.data = data;
    }

    public void readFromNBT(NBTTagCompound tag)
    {
        this.data.readFromNBT(tag);
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setShort("PunchedCardDataId", this.id);
        this.data.writeToNBT(tag);

        return tag;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        this.data.addInformation(stack, player, tooltip, advanced);
    }

    public short getId()
    {
        return this.id;
    }

    public IPunchedCard getData()
    {
        return this.data;
    }
}
