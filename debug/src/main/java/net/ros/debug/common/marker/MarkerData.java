package net.ros.debug.common.marker;

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
}
