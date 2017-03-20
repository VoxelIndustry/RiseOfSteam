package net.qbar.common.multiblock.blueprint;

import net.qbar.client.render.tile.VisibilityModelState;

public class MultiblockStep
{
    private String[]             parts;

    private VisibilityModelState opaqueState, alphaState;

    public MultiblockStep()
    {

    }

    public String[] getParts()
    {
        return this.parts;
    }

    public void setParts(final String[] parts)
    {
        this.parts = parts;
    }

    public VisibilityModelState getOpaqueState()
    {
        return this.opaqueState;
    }

    public VisibilityModelState getAlphaState()
    {
        return this.alphaState;
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
