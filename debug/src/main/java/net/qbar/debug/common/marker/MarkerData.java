package net.qbar.debug.common.marker;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.math.BlockPos;

@Data
@AllArgsConstructor
public class MarkerData
{
    private BlockPos pos;
    private long startTime;
    private long duration;

    public float getTiming()
    {
        return duration / (System.currentTimeMillis() - startTime);
    }
}
