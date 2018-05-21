package net.ros.client.render.model.obj;

import lombok.Data;
import net.ros.common.grid.node.PipeType;
import net.ros.common.multiblock.MultiblockSide;

import java.util.HashMap;
import java.util.List;

@Data
public class ConnState
{
    private HashMap<MultiblockSide, PipeType> pipeMap = new HashMap<>();
}
