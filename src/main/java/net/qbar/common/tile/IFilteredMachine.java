package net.qbar.common.tile;

import net.qbar.common.card.FilterCard;

public interface IFilteredMachine
{
    boolean isWhitelist();

    default boolean isBlacklist()
    {
        return !this.isWhitelist();
    }

    void setWhitelist(boolean isWhitelist);

    FilterCard getFilter();
}