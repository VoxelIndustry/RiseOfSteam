package net.qbar.debug.common.marker;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class MarkerDataHandler
{
    @Getter
    private static final Set<MarkerData> markersList = Sets.newConcurrentHashSet();

    public static void push(BlockPos pos, Long lifetime)
    {
        markersList.add(new MarkerData(pos, System.currentTimeMillis(), lifetime));
    }
}
