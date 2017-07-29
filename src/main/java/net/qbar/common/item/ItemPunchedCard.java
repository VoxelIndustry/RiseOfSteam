package net.qbar.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPunchedCard extends ItemBase
{
    public ItemPunchedCard()
    {
        super("punched_card");
        this.setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = PunchedCardDataManager.getInstance().readFromNBT(stack.getTagCompound());
            if (card.isValid(stack.getTagCompound()))
                card.addInformation(stack, tooltip, flag);
            else
                tooltip.add("Card Invalid");
        }
        else
            tooltip.add("Card Empty");
    }
}