package net.ros.common.compat.jei;

import lombok.Getter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.ros.client.render.RenderUtil;
import net.ros.common.ROSConstants;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.blueprint.Blueprint;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.BrokkGuiPlatform;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.wrapper.GuiRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlueprintRecipeWrapper implements IRecipeWrapper
{
    @Getter
    private Blueprint blueprint;
    @Getter
    private ItemStack machine;

    private ITickTimer wrenchTimer;
    private ITickTimer stepTimer;

    private int blockWidth;
    private int blockHeight;

    private ResourceLocation wrenchTex = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/jei/wrench.png");

    BlueprintRecipeWrapper(Blueprint blueprint, IGuiHelper guiHelper)
    {
        this.blueprint = blueprint;

        this.wrenchTimer = guiHelper.createTickTimer(20, 5, false);

        int steps = blueprint.getMultiblockSteps().size() - 1;
        this.stepTimer = guiHelper.createTickTimer(20 * steps, steps - 1, true);

        this.machine = new ItemStack(Block.getBlockFromName("ros:" + this.blueprint.getDescriptor().getName()));

        this.blockWidth = this.blueprint.getDescriptor().get(MultiblockComponent.class).getLength();
        this.blockHeight = this.blueprint.getDescriptor().get(MultiblockComponent.class).getHeight();
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY)
    {
        if (mouseX > 128 && mouseY > 0 && mouseX < 128 + 16 * blockWidth && mouseY < 16 * blockHeight)
            return Collections.singletonList(this.machine.getDisplayName());
        return Collections.emptyList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bindTexture(wrenchTex);

        double offset = 1 / 5D * wrenchTimer.getValue();

        this.drawTexturedRect(112, 12, 0, offset, 1, offset + 1 / 5D, 16, 16);

        int count = this.blueprint.getSteps().size();

        GlStateManager.pushAttrib();

        for (int i = 0; i < count; i++)
        {
            BrokkGuiPlatform.getInstance().getGuiHelper().drawColoredRect(new GuiRenderer(Tessellator.getInstance()),
                    2, 20 + i * 20, 92, 2, 0, Color.GRAY);

            minecraft.fontRenderer.drawString(String.valueOf(blueprint.getStepsTime().get(i)),
                    96, 20 - (minecraft.fontRenderer.FONT_HEIGHT / 2) + i * 20, 0);

        }

        if (blueprint.getMultiblockSteps().size() > 1)
        {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GlStateManager.pushMatrix();
            GlStateManager.translate(132 + 5 * blockWidth, 12 + 8 * blockHeight, 30);

            GlStateManager.rotate(225, 0, 1, 0);
            GlStateManager.rotate(15, 1, 0, 0);
            GlStateManager.rotate(195, 0, 0, 1);

            GlStateManager.scale(10, 10, 10);
            RenderUtil.renderMultiblock(Block.getBlockFromItem(this.machine.getItem()).getDefaultState(), 0, 0, 0,
                    Minecraft.getMinecraft().getBlockRendererDispatcher(), Collections.emptyList(),
                    blueprint.getMultiblockSteps().get(blueprint.getMultiblockSteps().size() - 1
                            - stepTimer.getValue()).getOpaqueState());
            GlStateManager.popMatrix();
        }
        GlStateManager.popAttrib();
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<ItemStack> ingredientStacks = new ArrayList<>();
        ingredientStacks.add(this.blueprint.getRodStack());

        this.blueprint.getSteps().stream().flatMap(Collection::stream).forEach(ingredientStacks::add);

        ingredients.setInputs(ItemStack.class, ingredientStacks);
        ingredients.setOutputs(ItemStack.class, Collections.singletonList(this.machine));
    }

    private void drawTexturedRect(double x, double y, double u, double v, double s, double t, double width,
                                  double height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, (y + height), 0.0D).tex(u, t).endVertex();
        bufferbuilder.pos((x + width), (y + height), 0.0D).tex(s, t).endVertex();
        bufferbuilder.pos((x + width), y, 0.0D).tex(s, v).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u, v).endVertex();
        tessellator.draw();
    }
}
