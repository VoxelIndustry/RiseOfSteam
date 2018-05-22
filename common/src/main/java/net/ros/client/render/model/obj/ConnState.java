package net.ros.client.render.model.obj;

import lombok.Data;
import net.ros.common.grid.node.PipeType;
import net.ros.common.multiblock.MultiblockSide;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

@Data
public class ConnState
{
    // Map containing a MultiblockSide as key and a pair of PipeType and pipe variant key as value
    private HashMap<MultiblockSide, Pair<PipeType, String>> pipeMap = new HashMap<>();
}
