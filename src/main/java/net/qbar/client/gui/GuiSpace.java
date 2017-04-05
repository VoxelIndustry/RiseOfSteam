package net.qbar.client.gui;

public class GuiSpace
{
    private final int x, y, width, height;

    public GuiSpace(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

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
