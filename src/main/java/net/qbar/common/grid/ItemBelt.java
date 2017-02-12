package net.qbar.common.grid;

import org.lwjgl.util.vector.Vector2f;

import net.minecraft.item.ItemStack;

public class ItemBelt
{
    private final ItemStack stack;
    private Vector2f        pos;

    public ItemBelt(final ItemStack stack, final Vector2f pos)
    {
        this.stack = stack;
        this.pos = pos;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public Vector2f getPos()
    {
        return this.pos;
    }

    public void setPos(final Vector2f pos)
    {
        this.pos = pos;
    }
}
