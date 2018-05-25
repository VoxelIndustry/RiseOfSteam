package net.ros.debug.common.counter;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CounterDataHandler
{
    @Getter
    private static final ConcurrentHashMap<BlockPos, List<CounterData>> countersMap = new ConcurrentHashMap<>();

    public static void push(BlockPos pos, String key, Long lifetime)
    {
        if (!countersMap.containsKey(pos))
            countersMap.put(pos, Lists.newArrayList());

        Optional<CounterData> existing = countersMap.get(pos).stream().filter(data -> data.getKey().equals(key))
                .findFirst();

        if (!existing.isPresent())
        {
            countersMap.get(pos).add(new CounterData(pos, System.currentTimeMillis(), lifetime, 1, key));
        }
        else
        {
            existing.get().setCount(existing.get().getCount() + 1);
            existing.get().setStartTime(System.currentTimeMillis());
        }
    }
}
