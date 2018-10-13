package net.ros.common.card;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.util.ItemUtils;

import java.util.*;

@Getter
public class CraftCard implements IPunchedCard
{
    private final int ID;

    private ItemStack[]     recipe           = new ItemStack[9];
    private List<ItemStack> compressedRecipe = new ArrayList<>();

    @Setter
    private ItemStack result = ItemStack.EMPTY;

    public CraftCard(final int ID)
    {
        this.ID = ID;
        for (int i = 0; i < this.recipe.length; i++)
            this.recipe[i] = ItemStack.EMPTY;
    }

    public void setIngredient(int slot, ItemStack ingredient)
    {
        if (!ingredient.isEmpty())
        {
            Optional<ItemStack> existing = compressedRecipe.stream()
                    .filter(stack -> ItemUtils.deepEquals(ingredient, stack)).findAny();

            if (existing.isPresent())
                existing.get().grow(ingredient.getCount());
            else
                this.compressedRecipe.add(ingredient.copy());
        }

        this.recipe[slot] = ingredient;
    }

    @Override
    public IPunchedCard readFromNBT(final NBTTagCompound tag)
    {
        final CraftCard card = new CraftCard(CardDataStorage.ECardType.CRAFT.getID());

        card.result = new ItemStack((NBTTagCompound) tag.getTag("stack_result"));

        for (int i = 0; i < card.recipe.length; i++)
            card.setIngredient(i, new ItemStack((NBTTagCompound) tag.getTag("stack_" + i)));
        return card;
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag)
    {
        final NBTTagCompound res = new NBTTagCompound();
        this.result.writeToNBT(res);
        tag.setTag("stack_result", res);

        for (int i = 0; i < this.recipe.length; i++)
        {
            final NBTTagCompound stack = new NBTTagCompound();
            this.recipe[i].writeToNBT(stack);
            tag.setTag("stack_" + i, stack);
        }
    }

    @Override
    public void addInformation(final ItemStack stack, final List<String> tooltip, final ITooltipFlag flag)
    {
        tooltip.add("Recipe: ");
        if (!flag.isAdvanced())
            return;

        for (final ItemStack element : this.compressedRecipe)
        {
            if (!element.isEmpty())
                tooltip.add(ItemUtils.getPrettyStackName(element));
        }
        tooltip.add("");
        tooltip.add(" -> " + ItemUtils.getPrettyStackName(this.result));

    }

    @Override
    public boolean isValid(final NBTTagCompound tag)
    {
        boolean res = tag.hasKey("stack_result");

        for (int i = 0; i < this.recipe.length && res; i++)
            res = tag.hasKey("stack_" + i);

        return res;
    }

    @Override
    public int getID()
    {
        return this.ID;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CraftCard craftCard = (CraftCard) o;
        return ID == craftCard.ID && recipeEquals(craftCard.recipe) &&
                ItemUtils.deepEquals(result, craftCard.result);
    }

    @Override
    public int hashCode()
    {
        int result1 = Objects.hash(ID, result);
        result1 = 31 * result1 + Arrays.hashCode(recipe);
        return result1;
    }

    private boolean recipeEquals(ItemStack[] recipe)
    {
        if (recipe.length != this.recipe.length)
            return false;

        int i = 0;
        for (ItemStack stack : recipe)
        {
            if (!ItemUtils.deepEquals(stack, this.recipe[i]))
                return false;
            i++;
        }
        return true;
    }
}
