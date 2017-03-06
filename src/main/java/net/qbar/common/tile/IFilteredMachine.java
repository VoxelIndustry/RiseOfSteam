package net.qbar.common.tile;

import net.minecraft.util.EnumFacing;
import net.qbar.common.card.FilterCard;

public interface IFilteredMachine
{
    boolean isWhitelist(EnumFacing facing);

    default boolean isBlacklist(final EnumFacing facing)
    {
        return !this.isWhitelist(facing);
    }

    void setWhitelist(EnumFacing facing, boolean isWhitelist);

    FilterCard getFilter(EnumFacing facing);
}