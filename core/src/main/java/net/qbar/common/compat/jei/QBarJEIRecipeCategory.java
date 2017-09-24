package net.qbar.common.compat.jei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.qbar.QBar;

import java.util.ArrayList;
import java.util.List;

public class QBarJEIRecipeCategory implements IRecipeCategory<QBarJEIRecipeWrapper>
{
    @Getter
    private final IDrawable background;
    @Getter
    private final String    title;
    @Getter
    private final String    uid;

    private final List<JEISlot> slots;

    public QBarJEIRecipeCategory(final IGuiHelper guiHelper, ResourceLocation background, int u, int v, int width,
                                 int height, String title, String uid, List<JEISlot> slots)
    {
        this.background = guiHelper.createDrawable(background, u, v, width, height);
        this.title = I18n.translateToLocal(title);
        this.uid = uid;
        this.slots = slots;
    }

    public static Builder builder(IGuiHelper guiHelper)
    {
        return new Builder(guiHelper);
    }

    @Override
    public String getModName()
    {
        return QBar.MODNAME;
    }

    @Override
    public void setRecipe(final IRecipeLayout recipeLayout, final QBarJEIRecipeWrapper recipeWrapper,
                          final IIngredients ingredients)
    {
        final IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        slots.forEach(slot -> {
            guiItemStacks.init(this.slots.indexOf(slot), slot.isInput(), slot.getX(), slot.getY());

            int count = this.getSlotCount(slot);
            if (slot.isInput())
            {
                if (ingredients.getInputs(ItemStack.class).size() > count)
                    guiItemStacks.set(this.slots.indexOf(slot), ingredients.getInputs(ItemStack.class).get(count));
            }
            else
            {
                if (ingredients.getOutputs(ItemStack.class).size() > count)
                    guiItemStacks.set(this.slots.indexOf(slot), ingredients.getOutputs(ItemStack.class).get(count));
            }
        });
    }

    private int getSlotCount(JEISlot from)
    {
        int i;
        int count = 0;
        for (i = 0; i < this.slots.indexOf(from); i++)
        {
            if (from.isInput() && this.slots.get(i).isInput() || !from.isInput() && !this.slots.get(i).isInput())
                count++;
        }
        return count;
    }

    public static class Builder
    {
        private IGuiHelper guiHelper;

        private ResourceLocation background;
        private int              u, v, width, height;
        private       String        title;
        private       String        uid;
        private final List<JEISlot> slots;

        public Builder(IGuiHelper guiHelper)
        {
            this.guiHelper = guiHelper;
            this.slots = new ArrayList<>();
        }

        public Builder input(int x, int y)
        {
            this.slots.add(new JEISlot(x, y, true));
            return this;
        }

        public Builder output(int x, int y)
        {
            this.slots.add(new JEISlot(x, y, false));
            return this;
        }

        public Builder background(ResourceLocation background)
        {
            this.background = background;
            return this;
        }

        public Builder u(int u)
        {
            this.u = u;
            return this;
        }

        public Builder v(int v)
        {
            this.v = v;
            return this;
        }

        public Builder width(int width)
        {
            this.width = width;
            return this;
        }

        public Builder height(int height)
        {
            this.height = height;
            return this;
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder uid(String uid)
        {
            this.uid = uid;
            return this;
        }

        public QBarJEIRecipeCategory create()
        {
            return new QBarJEIRecipeCategory(guiHelper, background, u, v, width, height, title, uid, slots);
        }
    }

    @Data
    @AllArgsConstructor
    private static class JEISlot
    {
        private int     x;
        private int     y;
        private boolean input;
    }
}
