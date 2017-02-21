package net.qbar.common.card;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PunchedCardDataManager
{
    private static PunchedCardDataManager instance;

    public static PunchedCardDataManager getInstance()
    {
        if (instance == null)
            instance = new PunchedCardDataManager();
        return instance;
    }

    private Map<Short, PunchedCardData> datas;

    private PunchedCardDataManager()
    {
        datas = Maps.newHashMap();
    }

    public PunchedCardData registerDataType(int id, IPunchedCard data)
    {
        if (this.datas.containsKey(id))
            throw new IllegalArgumentException("id already used");
        PunchedCardData card = new PunchedCardData(id, data);

        this.datas.put(card.getId(), card);
        return card;
    }

    public PunchedCardData getCardData(ItemStack card)
    {
        NBTTagCompound tag = card.getTagCompound();
        if (!tag.hasKey("PunchedCardDataId"))
            throw new IllegalArgumentException("Invalid ItemStack");
        short id = tag.getShort("PunchedCardDataId");
        if (!this.datas.containsKey(id))
            throw new IllegalArgumentException("No data found");

        return this.datas.get(id);
    }
}
