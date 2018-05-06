package net.ros.debug.common.chip;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class ChipDataHandler
{
    @Getter
    private static final HashMap<BlockPos, ChipData> chipsMap = new HashMap<>();

    public static void increment(BlockPos pos, String key)
    {
        ChipDataHandler.increment(pos, key, 1);
    }

    public static void increment(BlockPos pos, String key, int value)
    {
        if (!chipsMap.containsKey(pos))
            chipsMap.put(pos, new ChipData());
        if (!chipsMap.get(pos).getCounters().containsKey(key))
            chipsMap.get(pos).getCounters().put(key, 0L);
        chipsMap.get(pos).getCounters().put(key, chipsMap.get(pos).getCounters().get(key) + value);
    }
}
