package net.qbar.common.grid;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@Getter
@Setter
public class ItemBelt
{
    private final ItemStack stack;
    private       float     posX;
    private       float     posY;
    private       float     prevPosX;
    private       float     prevPosY;


    public ItemBelt(final @Nonnull ItemStack stack, float posX, float posY)
    {
        this.stack = stack;
        this.posX = posX;
        this.posY = posY;

        this.prevPosX = posX;
        this.prevPosY = posY;
    }

    public void setPosX(float posX)
    {
        this.prevPosX = this.posX;
        this.posX = posX;
    }

    public void setPosY(float posY)
    {
        this.prevPosY = this.posY;
        this.posY = posY;
    }
}
