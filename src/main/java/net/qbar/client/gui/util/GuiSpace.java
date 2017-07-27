package net.qbar.client.gui.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuiSpace
{
    private final int x, y, width, height;

    public int getEndX()
    {
        return this.x + this.width;
    }

    public int getEndY()
    {
        return this.y + this.height;
    }

    public boolean isMouseInside(int mouseX, int mouseY)
    {
        return mouseX > this.getX() && mouseX < this.getEndX() && mouseY > this.getY() && mouseY < this.getEndY();
    }
}
