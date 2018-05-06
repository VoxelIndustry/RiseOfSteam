package net.ros.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.IPunchedCard;

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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound())
        {
            final IPunchedCard card = CardDataStorage.instance().read(stack.getTagCompound());
            if (card.isValid(stack.getTagCompound()))
                card.addInformation(stack, tooltip, flag);
            else
                tooltip.add("Card Invalid");
        }
        else
            tooltip.add("Card Empty");
    }

    @Override
    public void registerVariants()
    {
        this.addVariant("empty", new ModelResourceLocation(ROSConstants.MODID + ":punched_card", "inventory"));
        this.addVariant("used", new ModelResourceLocation(ROSConstants.MODID + ":punched_card_used", "inventory"));
        super.registerVariants();
    }

    @Override
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack ->
        {
            if (stack.hasTagCompound())
                return this.getVariantModel("used");
            return this.getVariantModel("empty");
        });

        super.registerModels();
    }

    @Override
    public boolean hasSpecialModel()
    {
        return true;
    }
}