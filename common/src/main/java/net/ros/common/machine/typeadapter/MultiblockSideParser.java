package net.ros.common.machine.typeadapter;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.ros.common.multiblock.MultiblockSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MultiblockSideParser
{
    private static List<String> KEYS = Arrays.asList("pos", "facing", "fromPos", "toPos");

    private BlockPos         from    = null;
    private BlockPos         to      = null;
    private BlockPos         unique  = null;
    private List<EnumFacing> facings = new ArrayList<>();

    public boolean isKey(String key)
    {
        return KEYS.contains(key);
    }

    public void parse(String key, JsonReader in) throws IOException
    {
        switch (key)
        {
            case "pos":
                in.beginArray();
                unique = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                in.endArray();
                break;
            case "facing":
                if (in.peek() == JsonToken.BEGIN_ARRAY)
                {
                    in.beginArray();
                    while (in.hasNext())
                        facings.add(EnumFacing.byName(in.nextString()));
                    in.endArray();
                }
                else
                    facings = Collections.singletonList(EnumFacing.byName(in.nextString()));
                break;
            case "fromPos":
                in.beginArray();
                from = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                in.endArray();
                break;
            case "toPos":
                in.beginArray();
                to = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                in.endArray();
                break;
            default:
                break;
        }
    }

    public List<MultiblockSide> get()
    {
        List<BlockPos> posList = Collections.singletonList(unique);

        if (from != null && to != null)
            posList = StreamSupport.stream(BlockPos.getAllInBox(from, to).spliterator(), false)
                    .collect(Collectors.toList());

        return posList.stream().flatMap(pos ->
                facings.stream().map(facing -> new MultiblockSide(pos, facing))).collect(Collectors.toList());
    }
}
