package net.ros.client.gui;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.ros.common.steam.ISteamTank;
import org.yggard.brokkgui.control.GuiToggleButton;
import org.yggard.brokkgui.control.GuiToggleGroup;
import org.yggard.brokkgui.internal.IGuiRenderer;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.ColorConstants;
import org.yggard.brokkgui.paint.RenderPass;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.shape.Rectangle;
import org.yggard.brokkgui.wrapper.elements.MCTooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PressureControlPane extends GuiAbsolutePane
{
    private float               maxPressure;
    private float               pressureStep;
    private float               stepCount;
    private BaseProperty<Float> currentStep;
    private Supplier<Float>     pressureGetter;
    private Consumer<Float>     pressureSetter;

    private Needle needle;

    private GuiToggleGroup toggleGroup;

    public PressureControlPane(float maxPressure, float pressureStep, Supplier<Float> pressureGetter,
                               Consumer<Float> pressureSetter, ISteamTank steamTank)
    {
        this.addStyleClass("pressure-panel");
        this.maxPressure = maxPressure;
        this.pressureStep = pressureStep;

        this.pressureGetter = pressureGetter;
        this.pressureSetter = pressureSetter;

        this.stepCount = maxPressure / pressureStep;
        this.currentStep = new BaseProperty<>(stepCount - 1, "currentStepProperty");

        this.setWidth(96);
        this.setHeight(64);

        this.needle = new Needle(steamTank);
        this.needle.setWidth(32);
        this.needle.setHeight(4);
        this.needle.addStyleClass("pressure-needle");

        this.addChild(needle, this.getWidth() / 2 - needle.getWidth(), this.getHeight() - needle.getHeight());

        this.toggleGroup = new GuiToggleGroup();

        for (int i = 0; i < stepCount; i++)
        {
            GuiToggleButton stepButton = new GuiToggleButton();
            stepButton.setBackgroundColor(getColor(i * pressureStep / maxPressure));
            stepButton.setWidth(6);
            stepButton.setHeight(6);
            stepButton.setTooltip(MCTooltip.build().line(String.valueOf(pressureStep * i)).create());
            stepButton.setToggleGroup(toggleGroup);
            stepButton.addStyleClass("pressure-square");

            float startX = -42;
            float startY = 0;
            float x =
                    (float) ((startX * Math.cos(Math.toRadians(180 * (i / (stepCount - 1))))) - (startY * Math.sin(Math.toRadians(180 * (i / (stepCount - 1))))));
            float y =
                    (float) ((startY * Math.cos(Math.toRadians(180 * (i / (stepCount - 1))))) + (startX * Math.sin(Math.toRadians(180 * (i / (stepCount - 1))))));
            this.addChild(stepButton, 45 + x, this.getHeight() - stepButton.getHeight() + y);
        }
    }

    private Color[] colors = new Color[]{Color.GREEN, Color.YELLOW, ColorConstants.getColor("orange"), Color.RED,
            Color.BLACK};

    private Color getColor(float ratio)
    {
        Color from = colors[(int) Math.floor((colors.length - 1) * ratio)];
        Color to = colors[(int) Math.ceil((colors.length - 1) * ratio)];

        if (from == to)
            return from;

        ratio = ratio * (colors.length - 2);
        float interpR = ((to.getRed() - from.getRed()) * ratio + from.getRed());
        float interpG = ((to.getGreen() - from.getGreen()) * ratio + from.getGreen());
        float interpB = ((to.getBlue() - from.getBlue()) * ratio + from.getBlue());

        return new Color(interpR, interpG, interpB);
    }

    private static final class Needle extends Rectangle
    {
        @Getter
        @Setter
        private float rotation;

        private ISteamTank steamTank;

        public Needle(ISteamTank steamTank)
        {
            this.rotation = 45;

            this.steamTank = steamTank;
        }

        @Override
        public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.getxPos() + this.getWidth(), this.getyPos() + this.getHeight() / 2, 0);
            GlStateManager.rotate(rotation, 0, 0, 1);
            GlStateManager.translate(-this.getxPos() - this.getWidth(), -this.getyPos() - this.getHeight() / 2, 0);

            super.renderContent(renderer, pass, mouseX, mouseY);

            GlStateManager.popMatrix();

            if (steamTank.getPressure() > steamTank.getMaxPressure())
                rotation = 180;
            else
                rotation = 180 * (steamTank.getPressure() / steamTank.getMaxPressure());
        }
    }
}
