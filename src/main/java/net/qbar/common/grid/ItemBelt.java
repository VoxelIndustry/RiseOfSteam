package net.qbar.common.grid;

import lombok.Getter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class ItemBelt
{
    @Nonnull
    @Getter
    private final ItemStack stack;
    @Nonnull
    @Getter
    private final Vector2f  pos;

    public ItemBelt(final @Nonnull ItemStack stack, final @Nonnull Vector2f pos)
    {
        this.stack = stack;
        this.pos = pos;
    }
}
