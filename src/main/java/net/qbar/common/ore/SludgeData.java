package net.qbar.common.ore;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

@Builder
public class SludgeData
{
    @Getter
    @Singular
    private final Map<QBarOre, Float> ores;

    public SludgeData addOre(QBarOre ore, float proportion)
    {
        if (this.ores.containsKey(ore))
            this.ores.put(ore, this.ores.get(ore) + proportion);
        else
            this.ores.put(ore, proportion);
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        int i = 0;
        for (Map.Entry<QBarOre, Float> ore : ores.entrySet())
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
        SludgeData sludge = new SludgeData(new HashMap<>());

        for (int i = 0; i < tag.getInteger("oreQty"); i++)
            sludge.addOre(QBarOres.getOreFromName(tag.getString("ore" + i)).get(), tag.getFloat("proportion" + i));
        return sludge;
    }
}
