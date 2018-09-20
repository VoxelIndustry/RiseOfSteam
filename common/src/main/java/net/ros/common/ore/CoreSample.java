package net.ros.common.ore;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class CoreSample
{
    private Map<Ore, Integer>           oreCount;
    private ListMultimap<BlockPos, Ore> oreHeightMap;

    public CoreSample()
    {
        this(new LinkedHashMap<>(), MultimapBuilder.linkedHashKeys().arrayListValues().build());
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        int i = 0;
        for (Map.Entry<Ore, Integer> entry : oreCount.entrySet())
        {
            tag.setString("oreCountKey" + i, entry.getKey().getName());
            tag.setInteger("oreCountValue" + i, entry.getValue());
            i++;
        }
        tag.setInteger("oreCount", i);

        i = 0;
        for (BlockPos pos : oreHeightMap.keySet())
        {
            tag.setLong("oreHeightMapKey" + i, pos.toLong());
            int j = 0;
            for (Ore ore : oreHeightMap.get(pos))
            {
                tag.setString("oreHeightMap" + i + ":" + j, ore.getName());
                j++;
            }
            tag.setInteger("oreHeightMapCount" + i, j);
            i++;
        }
        tag.setInteger("oreHeightMap", i);
        return tag;
    }

    public static CoreSample fromNBT(NBTTagCompound tag)
    {
        Map<Ore, Integer> oreCount = new LinkedHashMap<>();
        ListMultimap<BlockPos, Ore> oreHeightMap =
                MultimapBuilder.linkedHashKeys().arrayListValues().build();

        int count = tag.getInteger("oreCount");

        for (int i = 0; i < count; i++)
            oreCount.put(Ores.getOreFromName(tag.getString("oreCountKey" + i)).orElse(Ores.CASSITERITE),
                    tag.getInteger("oreCountValue" + i));

        count = tag.getInteger("oreHeightMap");

        for (int i = 0; i < count; i++)
        {
            int stackCount = tag.getInteger("oreHeightMapCount" + i);
            BlockPos pos = BlockPos.fromLong(tag.getLong("oreHeightMapKey" + i));

            for (int j = 0; j < stackCount; j++)
                oreHeightMap.put(pos,
                        Ores.getOreFromName(tag.getString("oreHeightMap" + i + ":" + j)).orElse(Ores.CASSITERITE));
        }

        return new CoreSample(oreCount, oreHeightMap);
    }
}
