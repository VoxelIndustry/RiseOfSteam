package net.ros.common.grid;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@Getter
@Setter
public class ItemBelt
{
    private final ItemStack stack;
    private       double    posX;
    private       double    posY;
    private       double    prevPosX;
    private       double    prevPosY;


    public ItemBelt(final @Nonnull ItemStack stack, double posX, double posY)
    {
        this.stack = stack;
        this.posX = posX;
        this.posY = posY;

        this.prevPosX = posX;
        this.prevPosY = posY;
    }

    public void setPosX(double posX)
    {
        this.prevPosX = this.posX;
        this.posX = posX;
    }

    public void setPosY(double posY)
    {
        this.prevPosY = this.posY;
        this.posY = posY;
    }
}
