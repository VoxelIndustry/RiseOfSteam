package net.ros.common.compat.jei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.ros.common.ROSConstants;

import java.util.ArrayList;
import java.util.List;

public class JEIRecipeCategory implements IRecipeCategory<JEIRecipeWrapper>
{
    @Getter
    private final IDrawable background;
    @Getter
    private final String    title;
    @Getter
    private final String    uid;

    private final List<JEISlot>     itemSlots;
    private final List<JEITankSlot> tankSlots;

    public JEIRecipeCategory(final IGuiHelper guiHelper, ResourceLocation background, int u, int v, int width,
                             int height, String title, String uid, List<JEISlot> itemSlots, List<JEITankSlot> tankSlots)
    {
        this.background = guiHelper.createDrawable(background, u, v, width, height);
        this.title = I18n.translateToLocal(title);
        this.uid = uid;
        this.itemSlots = itemSlots;
        this.tankSlots = tankSlots;
    }

    public static Builder builder(IGuiHelper guiHelper)
    {
        return new Builder(guiHelper);
    }

    @Override
    public String getModName()
    {
        return ROSConstants.MODNAME;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        itemSlots.forEach(slot ->
        {
            guiItemStacks.init(this.itemSlots.indexOf(slot), slot.isInput(), slot.getX(), slot.getY());

            int count = this.getItemSlotCount(slot);
            if (slot.isInput())
            {
                if (ingredients.getInputs(VanillaTypes.ITEM).size() > count)
                    guiItemStacks.set(this.itemSlots.indexOf(slot), ingredients.getInputs(ItemStack.class).get(count));
            }
            else
            {
                if (ingredients.getOutputs(VanillaTypes.ITEM).size() > count)
                    guiItemStacks.set(this.itemSlots.indexOf(slot), ingredients.getOutputs(ItemStack.class).get(count));
            }
        });

        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        tankSlots.forEach(slot ->
        {
            guiFluidStacks.init(this.tankSlots.indexOf(slot), slot.isInput(), slot.getX(), slot.getY(),
                    slot.getWidth(), slot.getHeight(), slot.getMaxCapacity(), true, null);

            int count = this.getTankSlotCount(slot);
            if (slot.isInput())
            {
                if (ingredients.getInputs(VanillaTypes.FLUID).size() > count)
                    guiFluidStacks.set(this.tankSlots.indexOf(slot),
                            ingredients.getInputs(VanillaTypes.FLUID).get(count));
            }
            else
            {
                if (ingredients.getOutputs(VanillaTypes.FLUID).size() > count)
                    guiFluidStacks.set(this.tankSlots.indexOf(slot),
                            ingredients.getOutputs(VanillaTypes.FLUID).get(count));
            }
        });
    }

    private int getItemSlotCount(JEISlot from)
    {
        int i;
        int count = 0;
        for (i = 0; i < this.itemSlots.indexOf(from); i++)
        {
            if (from.isInput() && this.itemSlots.get(i).isInput() || !from.isInput() && !this.itemSlots.get(i).isInput())
                count++;
        }
        return count;
    }

    private int getTankSlotCount(JEITankSlot from)
    {
        int i;
        int count = 0;
        for (i = 0; i < this.tankSlots.indexOf(from); i++)
        {
            if (from.isInput() && this.tankSlots.get(i).isInput() || !from.isInput() && !this.tankSlots.get(i).isInput())
                count++;
        }
        return count;
    }

    public static class Builder
    {
        private IGuiHelper guiHelper;

        private ResourceLocation background;
        private int              u, v, width, height;
        private       String            title;
        private       String            uid;
        private final List<JEISlot>     itemSlots;
        private final List<JEITankSlot> tankSlots;

        public Builder(IGuiHelper guiHelper)
        {
            this.guiHelper = guiHelper;
            this.itemSlots = new ArrayList<>();
            this.tankSlots = new ArrayList<>();
        }

        public Builder inputItem(int x, int y)
        {
            this.itemSlots.add(new JEISlot(x, y, true));
            return this;
        }

        public Builder outputItem(int x, int y)
        {
            this.itemSlots.add(new JEISlot(x, y, false));
            return this;
        }

        public Builder inputTank(int x, int y, int width, int height, int maxCapacity)
        {
            this.tankSlots.add(new JEITankSlot(x, y, width, height, maxCapacity, true));
            return this;
        }

        public Builder outputTank(int x, int y, int width, int height, int maxCapacity)
        {
            this.tankSlots.add(new JEITankSlot(x, y, width, height, maxCapacity, false));
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

        public JEIRecipeCategory create()
        {
            return new JEIRecipeCategory(guiHelper, background, u, v, width, height, title, uid, itemSlots, tankSlots);
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

    @Data
    @AllArgsConstructor
    private static class JEITankSlot
    {
        private int     x;
        private int     y;
        private int     width;
        private int     height;
        private int     maxCapacity;
        private boolean input;
    }
}
