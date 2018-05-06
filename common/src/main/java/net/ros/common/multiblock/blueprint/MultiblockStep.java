package net.ros.common.multiblock.blueprint;

import lombok.Getter;
import lombok.Setter;
import net.ros.client.render.tile.VisibilityModelState;

@Getter
public class MultiblockStep
{
    @Setter
    private String[] parts;

    private VisibilityModelState opaqueState, alphaState;

    public MultiblockStep()
    {

    }

    void reloadStates()
    {
        this.opaqueState = new VisibilityModelState();
        this.opaqueState.blacklist = false;

        this.alphaState = new VisibilityModelState();
        this.alphaState.blacklist = true;

        for (final String part : this.parts)
        {
            this.opaqueState.parts.add(part);
            this.alphaState.parts.add(part);
        }
    }
}
