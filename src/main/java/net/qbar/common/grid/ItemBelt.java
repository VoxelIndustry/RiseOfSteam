package net.qbar.common.grid;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class ItemBelt
{
    @Nonnull
    private final ItemStack stack;
    @Nonnull
    private final Vector2f  pos;

    public ItemBelt(final @Nonnull ItemStack stack, final @Nonnull Vector2f pos)
    {
        this.stack = stack;
        this.pos = pos;
    }

    @Nonnull
    public ItemStack getStack()
    {
        return this.stack;
    }

    @Nonnull
    public Vector2f getPos()
    {
        return this.pos;
    }
}
