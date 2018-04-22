package net.qbar.common.block.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum BeltDirection implements IStringSerializable
{
    NORTH, EAST, SOUTH, WEST;

    public BeltDirection getOpposite()
    {
        switch (this)
        {
            case EAST:
                return BeltDirection.EAST;
            case NORTH:
                return BeltDirection.SOUTH;
            case SOUTH:
                return BeltDirection.NORTH;
            case WEST:
                return BeltDirection.WEST;
            default:
                return NORTH;
        }
    }

    public static BeltDirection getOrientation(final int value)
    {
        switch (value)
        {
            case 0:
                return BeltDirection.NORTH;
            case 1:
                return BeltDirection.EAST;
            case 2:
                return BeltDirection.SOUTH;
            default:
                return BeltDirection.WEST;
        }
    }

    @Override
    public String getName()
    {
        return this.name().toLowerCase();
    }

    public EnumFacing toFacing()
    {
        switch (this)
        {
            case EAST:
                return EnumFacing.EAST;
            case NORTH:
                return EnumFacing.NORTH;
            case SOUTH:
                return EnumFacing.SOUTH;
            case WEST:
                return EnumFacing.WEST;
            default:
                return EnumFacing.NORTH;
        }
    }

    public static BeltDirection fromFacing(final EnumFacing facing)
    {
        switch (facing)
        {
            case EAST:
                return EAST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            default:
                return NORTH;
        }
    }
}
