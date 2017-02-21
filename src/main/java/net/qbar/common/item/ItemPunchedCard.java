package net.qbar.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.card.PunchedCardData;
import net.qbar.common.card.PunchedCardDataManager;

public class ItemPunchedCard extends ItemBase
{
    public ItemPunchedCard()
    {
        super("punched_card");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        try
        {
            PunchedCardData card = PunchedCardDataManager.getInstance().getCardData(stack);
            if (card.isValid(stack.getTagCompound()))
                card.addInformation(stack, player, tooltip, advanced);
            else
                tooltip.add("Card Invalid");
        } catch (Exception e)
        {
            tooltip.add("Card Invalid");
        }
    }
}