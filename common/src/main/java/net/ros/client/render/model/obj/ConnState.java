package net.ros.client.render.model.obj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.ros.common.grid.node.PipeType;
import net.ros.common.multiblock.MultiblockSide;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
public class ConnState
{
    // Map containing a MultiblockSide as key and a pair of PipeType and pipe variant key as value
    private Map<MultiblockSide, Pair<PipeType, String>> pipeMap;

    public ConnState()
    {
        this.pipeMap = new HashMap<>();
    }

    public void addPipe(MultiblockSide side, PipeType type, String variantKey)
    {
        this.pipeMap.put(side, Pair.of(type, variantKey));
    }

    public void removePipe(MultiblockSide side)
    {
        this.pipeMap.remove(side);
    }
}
