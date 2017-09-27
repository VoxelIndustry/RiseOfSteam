package net.qbar.common.ore;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class SludgeData
{
    @Getter
    private Map<QBarMineral, Float> ores;

    public SludgeData()
    {
        this.ores = new HashMap<>();
    }

    public SludgeData addOre(QBarMineral ore, float proportion)
    {
        if (this.ores.containsKey(ore))
            this.ores.put(ore, this.ores.get(ore) + proportion);
        else
            this.ores.put(ore, proportion);
        return this;
    }

    public SludgeData addOres(Map<QBarMineral, Float> ores)
    {
        ores.forEach(this::addOre);
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        int i = 0;
        for (Map.Entry<QBarMineral, Float> ore : ores.entrySet())
        {
            tag.setString("ore" + i, ore.getKey().getName());
            tag.setFloat("proportion" + i, ore.getValue());
            i++;
        }
        tag.setInteger("oreQty", i);
        return tag;
    }

    public static SludgeData fromNBT(NBTTagCompound tag)
    {
        SludgeData sludge = new SludgeData();

        for (int i = 0; i < tag.getInteger("oreQty"); i++)
            sludge.addOre(QBarOres.getMineralFromName(tag.getString("ore" + i)).get(), tag.getFloat("proportion" + i));
        return sludge;
    }
}
