package net.ros.client.gui;

import fr.ourten.teabeans.binding.BaseBinding;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.ros.common.steam.ISteamTank;
import net.ros.common.steam.SteamUtil;
import net.voxelindustry.brokkgui.data.RectAlignment;
import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.data.Rotation;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.internal.IGuiRenderer;
import net.voxelindustry.brokkgui.paint.RenderPass;
import net.voxelindustry.brokkgui.paint.Texture;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.shape.Rectangle;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PressureControlPane extends GuiAbsolutePane
{
    private static final Texture TEX_BARRIER = new Texture("textures/items/barrier.png");

    private float           maxPressure;
    private float           cachedMinPressure;
    private float           cachedMaxPressure;
    private Supplier<Float> maxPressureGetter;
    private Supplier<Float> minPressureGetter;

    private Needle   needle;
    private GuiLabel maxPressureLabel;
    private GuiLabel minPressureLabel;

    public PressureControlPane(float maxPressure, float pressureStep, Supplier<Float> maxPressureGetter,
                               Consumer<Float> maxPressureSetter, ISteamTank steamTank)
    {
        this(maxPressure, pressureStep, maxPressureGetter, maxPressureSetter, null, null, steamTank);
    }

    public PressureControlPane(float maxPressure, float pressureStep, Supplier<Float> maxPressureGetter,
                               Consumer<Float> maxPressureSetter, Supplier<Float> minPressureGetter,
                               Consumer<Float> minPressureSetter, ISteamTank steamTank)
    {
        this.addStyleClass("pressure-panel");
        this.maxPressure = maxPressure;

        this.maxPressureGetter = maxPressureGetter;
        this.minPressureGetter = minPressureGetter;

        this.setWidth(96);
        this.setHeight(66);

        GuiLabel pressureLabel = new GuiLabel("");
        pressureLabel.setTextAlignment(RectAlignment.MIDDLE_CENTER);
        pressureLabel.setWidth(this.getWidth());

        this.addChild(pressureLabel, 0, this.getHeight() / 2 - 2);

        this.needle = new Needle(steamTank, pressureLabel);
        this.needle.setWidth(46);
        this.needle.setHeight(4);
        this.needle.addStyleClass("pressure-needle");

        this.addChild(needle, this.getWidth() / 2 - needle.getWidth() + 2, this.getHeight() - needle.getHeight() - 16);

        TextFormatting[] textColors = new TextFormatting[]{TextFormatting.GREEN, TextFormatting.YELLOW,
                TextFormatting.GOLD, TextFormatting.RED, TextFormatting.DARK_PURPLE};

        if (this.minPressureGetter != null)
            setupMinMaxPressureFields(pressureStep, maxPressureGetter, maxPressureSetter, minPressureGetter,
                    minPressureSetter);
        else
            setupMaxPressureField(pressureStep, maxPressureGetter, maxPressureSetter);
    }

    private void setupMaxPressureField(float pressureStep, Supplier<Float> maxPressureGetter,
                                       Consumer<Float> maxPressureSetter)
    {
        maxPressureLabel = new GuiLabel(SteamUtil.pressureFormat.format(maxPressureGetter.get()));
        maxPressureLabel.addStyleClass("pressure-field");
        maxPressureLabel.setWidth(30);
        maxPressureLabel.setHeight(14);
        maxPressureLabel.setTextAlignment(RectAlignment.MIDDLE_UP);
        maxPressureLabel.setTextPadding(new RectOffset(2, 0, 0, 0));

        this.addChild(maxPressureLabel, this.getWidth() / 2 - maxPressureLabel.getWidth() / 2,
                this.getHeight() - maxPressureLabel.getHeight());

        GuiButton lessButton = new GuiButton();
        lessButton.setWidth(9);
        lessButton.setHeight(14);
        lessButton.addStyleClass("pressure-less");
        this.addChild(lessButton, this.getWidth() / 2 - maxPressureLabel.getWidth() / 2 - lessButton.getWidth(),
                this.getHeight() - maxPressureLabel.getHeight());
        lessButton.setOnActionEvent(e -> maxPressureSetter.accept(maxPressureGetter.get() - pressureStep));

        GuiButton moreButton = new GuiButton();
        moreButton.setWidth(9);
        moreButton.setHeight(14);
        moreButton.addStyleClass("pressure-more");
        this.addChild(moreButton, this.getWidth() / 2 + maxPressureLabel.getWidth() / 2,
                this.getHeight() - maxPressureLabel.getHeight());
        moreButton.setOnActionEvent(e -> maxPressureSetter.accept(maxPressureGetter.get() + pressureStep));

        lessButton.getDisabledProperty().bind(new BaseBinding<Boolean>()
        {
            {
                super.bind(maxPressureLabel.getTextProperty());
            }

            @Override
            public Boolean computeValue()
            {
                return Float.parseFloat(maxPressureLabel.getText()) == 0;
            }
        });

        moreButton.getDisabledProperty().bind(new BaseBinding<Boolean>()
        {
            {
                super.bind(maxPressureLabel.getTextProperty());
            }

            @Override
            public Boolean computeValue()
            {
                return Float.parseFloat(maxPressureLabel.getText()) == maxPressure;
            }
        });
    }

    private void setupMinMaxPressureFields(float pressureStep, Supplier<Float> maxPressureGetter,
                                           Consumer<Float> maxPressureSetter, Supplier<Float> minPressureGetter,
                                           Consumer<Float> minPressureSetter)
    {
        // Min pressure field

        minPressureLabel = new GuiLabel(SteamUtil.pressureFormat.format(minPressureGetter.get()));
        minPressureLabel.addStyleClass("pressure-field");
        minPressureLabel.setWidth(30);
        minPressureLabel.setHeight(14);
        minPressureLabel.setTextAlignment(RectAlignment.MIDDLE_UP);
        minPressureLabel.setTextPadding(new RectOffset(2, 0, 0, 0));

        this.addChild(minPressureLabel, 9, this.getHeight() - minPressureLabel.getHeight());

        GuiButton lessMinButton = new GuiButton();
        lessMinButton.setWidth(9);
        lessMinButton.setHeight(14);
        lessMinButton.addStyleClass("pressure-less");
        this.addChild(lessMinButton, 0, this.getHeight() - minPressureLabel.getHeight());
        lessMinButton.setOnActionEvent(e -> minPressureSetter.accept(minPressureGetter.get() - pressureStep));

        GuiButton moreMinButton = new GuiButton();
        moreMinButton.setWidth(9);
        moreMinButton.setHeight(14);
        moreMinButton.addStyleClass("pressure-more");
        this.addChild(moreMinButton, lessMinButton.getWidth() + minPressureLabel.getWidth(),
                this.getHeight() - minPressureLabel.getHeight());
        moreMinButton.setOnActionEvent(e -> minPressureSetter.accept(minPressureGetter.get() + pressureStep));

        // Max Pressure field

        maxPressureLabel = new GuiLabel(SteamUtil.pressureFormat.format(maxPressureGetter.get()));
        maxPressureLabel.addStyleClass("pressure-field");
        maxPressureLabel.setWidth(30);
        maxPressureLabel.setHeight(14);
        maxPressureLabel.setTextAlignment(RectAlignment.MIDDLE_UP);
        maxPressureLabel.setTextPadding(new RectOffset(2, 0, 0, 0));

        this.addChild(maxPressureLabel, this.getWidth() - lessMinButton.getWidth() - maxPressureLabel.getWidth(),
                this.getHeight() - maxPressureLabel.getHeight());

        GuiButton lessMaxButton = new GuiButton();
        lessMaxButton.setWidth(9);
        lessMaxButton.setHeight(14);
        lessMaxButton.addStyleClass("pressure-less");
        this.addChild(lessMaxButton, this.getWidth() - maxPressureLabel.getWidth() - lessMaxButton.getWidth() * 2,
                this.getHeight() - maxPressureLabel.getHeight());
        lessMaxButton.setOnActionEvent(e -> maxPressureSetter.accept(maxPressureGetter.get() - pressureStep));

        GuiButton moreMaxButton = new GuiButton();
        moreMaxButton.setWidth(9);
        moreMaxButton.setHeight(14);
        moreMaxButton.addStyleClass("pressure-more");
        this.addChild(moreMaxButton, this.getWidth() - moreMaxButton.getWidth(),
                this.getHeight() - maxPressureLabel.getHeight());
        moreMaxButton.setOnActionEvent(e -> maxPressureSetter.accept(maxPressureGetter.get() + pressureStep));

        lessMinButton.getDisabledProperty().bind(new BaseBinding<Boolean>()
        {
            {
                super.bind(minPressureLabel.getTextProperty());
            }

            @Override
            public Boolean computeValue()
            {
                return Float.parseFloat(minPressureLabel.getText()) == 0;
            }
        });

        BaseBinding<Boolean> minUnderMaxBinding = new BaseBinding<Boolean>()
        {
            {
                super.bind(minPressureLabel.getTextProperty());
                super.bind(maxPressureLabel.getTextProperty());
            }

            @Override
            public Boolean computeValue()
            {
                float minPressureValue = Float.parseFloat(minPressureLabel.getText());
                float maxPressureValue = Float.parseFloat(maxPressureLabel.getText());

                return minPressureValue >= maxPressureValue;
            }
        };

        moreMinButton.getDisabledProperty().bind(minUnderMaxBinding);
        lessMaxButton.getDisabledProperty().bind(minUnderMaxBinding);

        moreMaxButton.getDisabledProperty().bind(new BaseBinding<Boolean>()
        {
            {
                super.bind(maxPressureLabel.getTextProperty());
            }

            @Override
            public Boolean computeValue()
            {
                return Float.parseFloat(maxPressureLabel.getText()) == maxPressure;
            }
        });
    }

    @Override
    public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
    {
        super.renderContent(renderer, pass, mouseX, mouseY);

        if (pass == RenderPass.BACKGROUND)
        {
            float angle = (float) Math.toRadians(180 * (1 - (cachedMaxPressure / maxPressure)));
            this.drawSector(this.getxPos() + this.getWidth() / 2, this.getyPos() + 48, 44, angle, 15);

            float barrierX;
            float barrierY;
            // Do not draw barrier with a sector of 25 degrees or less
            if (angle > Math.PI / 7)
            {
                barrierX = (float) (35 * Math.cos(Math.PI - angle / 2));
                barrierY = (float) (35 * Math.sin(Math.PI - angle / 2));
                this.drawBarrier(renderer, this.getxPos() + this.getWidth() / 2 - 8 - barrierX,
                        this.getyPos() + this.getHeight() / 2 + 8 - barrierY);
            }

            float suppliedPressureLimit = maxPressureGetter.get();
            if (this.cachedMaxPressure != suppliedPressureLimit)
            {
                this.cachedMaxPressure = suppliedPressureLimit;
                maxPressureLabel.setText(SteamUtil.pressureFormat.format(suppliedPressureLimit));
            }

            if (this.minPressureGetter != null)
            {
                angle = (float) Math.toRadians(-180 * (cachedMinPressure / maxPressure));

                this.drawSector(this.getxPos() + this.getWidth() / 2, this.getyPos() + 48, -44, angle, 15);

                // Do not draw barrier with a sector of 25 degrees or less
                if (-angle > Math.PI / 7)
                {
                    barrierX = (float) (35 * Math.cos(angle / 2));
                    barrierY = (float) (35 * Math.sin(angle / 2));
                    this.drawBarrier(renderer, this.getxPos() + this.getWidth() / 2 - 8 - barrierX,
                            this.getyPos() + this.getHeight() / 2 + 8 + barrierY);
                }

                suppliedPressureLimit = minPressureGetter.get();
                if (this.cachedMinPressure != suppliedPressureLimit)
                {
                    this.cachedMinPressure = suppliedPressureLimit;
                    minPressureLabel.setText(SteamUtil.pressureFormat.format(suppliedPressureLimit));
                }
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
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.7f, 0, 0.6f, 0.2f);
        GlStateManager.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i < num_segments; i++)
        {
            // Invert triangle draw order for reversed sectors
            if (r < 0)
                GL11.glVertex2f(x + cx, y + cy);

            GL11.glVertex2f(cx, cy);

            if (r > 0)
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
        GlStateManager.enableTexture2D();
    }

    private static final class Needle extends Rectangle
    {
        private GuiLabel pressureLabel;

        private ISteamTank steamTank;

        public Needle(ISteamTank steamTank, GuiLabel pressureLabel)
        {
            this.pressureLabel = pressureLabel;

            this.steamTank = steamTank;

            // x-offset = width - height / 2
            // y-offset = height / 2
            this.setRotation(Rotation.build().angle(45).from(46 - 2, 2).create());
        }

        @Override
        public void renderContent(IGuiRenderer renderer, RenderPass pass, int mouseX, int mouseY)
        {
            super.renderContent(renderer, pass, mouseX, mouseY);

            if (pass != RenderPass.MAIN)
                return;
            if (steamTank.getPressure() > steamTank.getMaxPressure())
                this.getRotation().setAngle(180);
            else
                this.getRotation().setAngle(180 * (steamTank.getPressure() / steamTank.getMaxPressure()));

            pressureLabel.setText(SteamUtil.pressureFormat.format(steamTank.getPressure()) + " / " +
                    SteamUtil.pressureFormat.format(steamTank.getMaxPressure()));
        }
    }
}
