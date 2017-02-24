package net.qbar.common.init;

import net.qbar.common.card.PunchedCardData;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.type.CraftCard;
import net.qbar.common.card.type.FilterCard;

public class QBarCardType
{
    public static PunchedCardData filter;
    public static PunchedCardData craft;

    public static final void registerCardTypes()
    {
        PunchedCardDataManager manager = PunchedCardDataManager.getInstance();

        filter = manager.registerDataType(0, new FilterCard());
        craft = manager.registerDataType(1, new CraftCard());
    }
}
