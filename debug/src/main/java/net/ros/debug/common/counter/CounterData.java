package net.ros.debug.common.counter;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.math.BlockPos;

@Data
@AllArgsConstructor
public class CounterData
{
    private BlockPos pos;
    private long     startTime;
    private long     duration;
    private int      count;
    private String key;
}
