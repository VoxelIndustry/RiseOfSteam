package net.qbar.client.gui.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.qbar.client.gui.GuiTexturedSpace;

@Getter
@AllArgsConstructor
@Builder
public class GuiProgress
{
    private GuiTexturedSpace space;

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    private boolean revert;

    private StartDirection direction;

    public GuiProgress(GuiTexturedSpace space, boolean revert)
    {
        this.space = space;
        this.revert = revert;
    }

    public enum StartDirection
    {
        LEFT, RIGHT, TOP, BOTTOM;
    }
}
