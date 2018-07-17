package net.ros.client.gui;

import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.ros.common.steam.ISteamTank;
import org.yggard.brokkgui.internal.IGuiRenderer;
import org.yggard.brokkgui.paint.RenderPass;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.shape.Rectangle;

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

            rotation = 180 * (steamTank.getPressure() / steamTank.getMaxPressure());
        }
    }
}
