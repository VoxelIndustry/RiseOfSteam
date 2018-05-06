package net.ros.common.block.property;

import net.minecraft.util.IStringSerializable;

public enum BeltSlope implements IStringSerializable
{
    NORMAL, UP, DOWN;

    @Override
    public String getName()
    {
        return this.name().toLowerCase();
    }

    public static BeltSlope getOrientation(final int value)
    {
        switch (value)
        {
            case 0:
                return NORMAL;
            case 1:
                return UP;
            case 2:
                return DOWN;
        }
        return NORMAL;
    }

    public BeltSlope cycle()
    {
        switch (this)
        {
            case UP:
                return NORMAL;
            case NORMAL:
                return DOWN;
            case DOWN:
                return UP;
        }
        return NORMAL;
    }

    public boolean isSlope()
    {
        return this != NORMAL;
    }
}
