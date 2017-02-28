package net.qbar.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;

public class ItemPunchedCard extends ItemBase
{
    public ItemPunchedCard()
    {
        super("punched_card");
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List<String> tooltip,
            final boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound());
            if (card.isValid(stack.getTagCompound()))
                card.addInformation(stack, player, tooltip, advanced);
            else
                tooltip.add("Card Invalid");
        }
        else
            tooltip.add("Card Empty");
    }
}