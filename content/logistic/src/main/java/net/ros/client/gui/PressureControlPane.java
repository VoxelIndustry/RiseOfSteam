package net.ros.client.gui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.SteamUtil;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.data.EAlignment;
import org.yggard.brokkgui.data.RectOffset;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.element.GuiLabel;
import org.yggard.brokkgui.internal.IGuiRenderer;
import org.yggard.brokkgui.paint.RenderPass;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.shape.Rectangle;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PressureControlPane extends GuiAbsolutePane
{
    private static final Texture TEX_BARRIER = new Texture("textures/items/barrier.png");

    private float           maxPressure;
    private float           cachedPressureLimit;
    private Supplier<Float> pressureLimitGetter;

    private Needle   needle;
    private GuiLabel pressureLimitLabel;

    public PressureControlPane(float maxPressure, float pressureStep, Supplier<Float> pressureLimitGetter,
                               Consumer<Float> pressureLimitSetter, ISteamTank steamTank)
    {
        this.addStyleClass("pressure-panel");
        this.maxPressure = maxPressure;

        this.pressureLimitGetter = pressureLimitGetter;

        this.setWidth(96);
        this.setHeight(66);

        GuiLabel pressureLabel = new GuiLabel("");
        pressureLabel.setTextAlignment(EAlignment.MIDDLE_CENTER);
        pressureLabel.setWidth(this.getWidth());

        this.addChild(pressureLabel, 0, this.getHeight() / 2 - 2);

        this.needle = new Needle(steamTank, pressureLabel);
        this.needle.setWidth(46);
        this.needle.setHeight(4);
        this.needle.addStyleClass("pressure-needle");

        this.addChild(needle, this.getWidth() / 2 - needle.getWidth() + 2, this.getHeight() - needle.getHeight() - 16);

        TextFormatting[] textColors = new TextFormatting[]{TextFormatting.GREEN, TextFormatting.YELLOW,
                TextFormatting.GOLD, TextFormatting.RED, TextFormatting.DARK_PURPLE};

        pressureLimitLabel = new GuiLabel(SteamUtil.pressureFormat.format(pressureLimitGetter.get()));
        pressureLimitLabel.addStyleClass("pressure-field");
        pressureLimitLabel.setWidth(30);
        pressureLimitLabel.setHeight(14);
        pressureLimitLabel.setTextAlignment(EAlignment.MIDDLE_UP);
        pressureLimitLabel.setTextPadding(new RectOffset(2, 0, 0, 0));

        this.addChild(pressureLimitLabel, this.getWidth() / 2 - pressureLimitLabel.getWidth() / 2,
                this.getHeight() - pressureLimitLabel.getHeight());

        GuiButton lessButton = new GuiButton();
        lessButton.setWidth(9);
        lessButton.setHeight(14);
        lessButton.addStyleClass("pressure-less");
        this.addChild(lessButton, this.getWidth() / 2 - pressureLimitLabel.getWidth() / 2 - lessButton.getWidth(),
                this.getHeight() - pressureLimitLabel.getHeight());
        lessButton.setOnActionEvent(e -> pressureLimitSetter.accept(pressureLimitGetter.get() - pressureStep));

        GuiButton moreButton = new GuiButton();
        moreButton.setWidth(9);
        moreButton.setHeight(14);
        moreButton.addStyleClass("pressure-more");
        this.addChild(moreButton, this.getWidth() / 2 + pressureLimitLabel.getWidth() / 2,
                this.getHeight() - pressureLimitLabel.getHeight());
        moreButton.setOnActionEvent(e -> pressureLimitSetter.accept(pressureLimitGetter.get() + pressureStep));
    }

    @Override
    public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
    {
        super.renderContent(renderer, pass, mouseX, mouseY);

        if (pass == RenderPass.BACKGROUND)
        {
            float angle = (float) Math.toRadians(180 * (1 - (cachedPressureLimit / maxPressure)));
            this.drawSector(this.getxPos() + this.getWidth() / 2, this.getyPos() + 48, 44,
                    angle, 15);

            float endX = (float) (35 * Math.cos(Math.PI - angle/2));
            float endY = (float) (35 * Math.sin(Math.PI - angle/2));
            this.drawBarrier(renderer, this.getxPos() + this.getWidth() / 2 - 8 - endX,
                    this.getyPos() + this.getHeight() / 2 + 8 - endY);

            float suppliedPressureLimit = pressureLimitGetter.get();
            if (this.cachedPressureLimit != suppliedPressureLimit)
            {
                this.cachedPressureLimit = suppliedPressureLimit;
                pressureLimitLabel.setText(SteamUtil.pressureFormat.format(suppliedPressureLimit));
            }
        }
    }

    private void drawBarrier(IGuiRenderer renderer, float x, float y)
    {
        renderer.getHelper().bindTexture(TEX_BARRIER);
        renderer.getHelper().drawTexturedRect(renderer, x, y, 0, 0, 16, 16, 0);
    }

    private void drawSector(float cx, float cy, float r, float sector_angle, int num_segments)
    {
        float theta = -sector_angle / (float) (num_segments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;

        float x = r;
        float y = 0;

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(0.7f, 0, 0.6f, 0.2f);
        GlStateManager.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i < num_segments; i++)
        {
            GL11.glVertex2f(cx, cy);
            GL11.glVertex2f(x + cx, y + cy);

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        float endX = (float) (r * Math.cos(sector_angle));
        float endY = (float) (r * Math.sin(sector_angle));
        GL11.glVertex2f(cx + endX, cy - endY);
        GlStateManager.glEnd();

        x = r;
        y = 0;
        GlStateManager.glLineWidth(3);
        GlStateManager.color(0.8f, 0, 0f, 0.7f);
        GlStateManager.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i < num_segments; i++)
        {
            GL11.glVertex2f(x + cx, y + cy);

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }

        GL11.glVertex2f(cx + endX, cy - endY);
        GlStateManager.glEnd();
    }

    private static final class Needle extends Rectangle
    {
        @Getter
        @Setter
        private float    rotation;
        private GuiLabel pressureLabel;

        private ISteamTank steamTank;

        public Needle(ISteamTank steamTank, GuiLabel pressureLabel)
        {
            this.rotation = 45;
            this.pressureLabel = pressureLabel;

            this.steamTank = steamTank;
        }

        @Override
        public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.getxPos() + this.getWidth() - this.getHeight() / 2,
                    this.getyPos() + this.getHeight() / 2, 0);
            GlStateManager.rotate(rotation, 0, 0, 1);
            GlStateManager.translate(-this.getxPos() - this.getWidth() + this.getHeight() / 2,
                    -this.getyPos() - this.getHeight() / 2, 0);

            super.renderContent(renderer, pass, mouseX, mouseY);

            GlStateManager.popMatrix();

            if (pass != RenderPass.MAIN)
                return;
            if (steamTank.getPressure() > steamTank.getMaxPressure())
                rotation = 180;
            else
                rotation = 180 * (steamTank.getPressure() / steamTank.getMaxPressure());

            pressureLabel.setText(SteamUtil.pressureFormat.format(steamTank.getPressure()) + " / " +
                    SteamUtil.pressureFormat.format(steamTank.getMaxPressure()));
        }
    }
}
