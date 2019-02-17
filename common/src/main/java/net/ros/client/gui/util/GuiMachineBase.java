package net.ros.client.gui.util;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.ros.common.fluid.MultiFluidTank;
import net.ros.common.steam.ISteamTank;
import net.voxelindustry.brokkgui.BrokkGuiPlatform;
import net.voxelindustry.brokkgui.data.Vector2i;
import net.voxelindustry.brokkgui.paint.Color;
import net.voxelindustry.brokkgui.wrapper.GuiHelper;
import net.voxelindustry.brokkgui.wrapper.GuiRenderer;
import net.voxelindustry.steamlayer.container.IContainerProvider;
import net.voxelindustry.steamlayer.tile.TileBase;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class GuiMachineBase<T extends TileBase & IContainerProvider> extends GuiContainer
{
    public static final NumberFormat heatFormat;

    static
    {
        heatFormat = NumberFormat.getInstance();
        heatFormat.setMaximumFractionDigits(1);
        heatFormat.setMinimumFractionDigits(1);
    }

    private T machine;

    private final ResourceLocation                                         background;
    private final List<Triple<IFluidTank, ITankTooltipSupplier, GuiSpace>> fluidtanks;
    private final List<Pair<ISteamTank, GuiSpace>>                         steamtanks;

    private final List<Pair<MultiFluidTank, GuiSpace>> multiFluidTanks;

    private final List<Pair<Function<Integer, Integer>, GuiProgress>> animatedSprites;
    private final List<Pair<GuiSpace, Supplier<List<String>>>>        tooltips;

    private final List<Pair<GuiSpace, Runnable>>  buttons;
    private final List<Pair<Vector2i, String>>    labels;
    private final List<Pair<GuiSpace, ItemStack>> itemStacks;

    private final GuiRenderer renderer;

    public GuiMachineBase(EntityPlayer player, T tile, ResourceLocation background)
    {
        super(tile.createContainer(player));

        this.machine = tile;
        this.background = background;
        this.fluidtanks = new ArrayList<>();
        this.steamtanks = new ArrayList<>();
        this.multiFluidTanks = new ArrayList<>();
        this.animatedSprites = new ArrayList<>();
        this.tooltips = new ArrayList<>();
        this.buttons = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.itemStacks = new ArrayList<>();

        this.renderer = new GuiRenderer(Tessellator.getInstance());
    }

    protected void addFluidTank(IFluidTank fluidTank, int x, int y, int width, int height)
    {
        this.addFluidTank(fluidTank, null, x, y, width, height);
    }

    protected void addFluidTank(IFluidTank fluidTank, ITankTooltipSupplier tooltipSupplier, int x, int y, int width,
                                int height)
    {
        this.fluidtanks.add(Triple.of(fluidTank, tooltipSupplier, new GuiSpace(x, y, width, height)));
    }

    protected void addSteamTank(ISteamTank steamTank, int x, int y, int width, int height)
    {
        this.steamtanks.add(Pair.of(steamTank, new GuiSpace(x, y, width, height)));
    }

    protected void addMultiFluidTank(MultiFluidTank multiFluidTank, int x, int y, int width, int height)
    {
        this.multiFluidTanks.add(Pair.of(multiFluidTank, new GuiSpace(x, y, width, height)));
    }

    protected void addAnimatedSprite(Function<Integer, Integer> stateSupplier, int x, int y, int width, int height,
                                     int u, int v, GuiProgress.StartDirection direction, boolean reverted)
    {
        this.addAnimatedSprite(stateSupplier,
                GuiTexturedSpace.builder().x(x).y(y).width(width).height(height).u(u).v(v).s(width).t(height).build(),
                direction, reverted);
    }

    protected void addAnimatedSprite(Function<Integer, Integer> stateSupplier, GuiTexturedSpace space,
                                     GuiProgress.StartDirection direction, boolean reverted)
    {
        this.animatedSprites.add(Pair.of(stateSupplier,
                GuiProgress.builder().space(space).direction(direction).revert(reverted).build()));
    }

    protected void addAnimatedSprite(Function<Integer, Integer> stateSupplier, GuiProgress progress)
    {
        this.animatedSprites.add(Pair.of(stateSupplier, progress));
    }

    protected void addTooltip(GuiSpace space, Supplier<List<String>> textSupplier)
    {
        this.tooltips.add(Pair.of(space, textSupplier));
    }

    protected void addTooltip(int startX, int startY, int endX, int endY, Supplier<List<String>> textSupplier)
    {
        this.addTooltip(new GuiSpace(startX, startY, endX - startX, endY - startY), textSupplier);
    }

    protected String getHeatTooltip(Supplier<Float> heatSupplier, Supplier<Float> maxHeatSupplier)
    {
        return TextFormatting.GOLD + "" + heatFormat.format(heatSupplier.get()) + " / "
                + maxHeatSupplier.get() + " °C";
    }

    protected void addSimpleButton(GuiSpace space, Runnable action)
    {
        this.buttons.add(Pair.of(space, action));
    }

    protected void addLabel(int x, int y, String text)
    {
        this.labels.add(Pair.of(new Vector2i(x, y), text));
    }

    protected void addItemStack(GuiSpace space, ItemStack stack)
    {
        this.itemStacks.add(Pair.of(space, stack));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0.0F);

        for (Triple<IFluidTank, ITankTooltipSupplier, GuiSpace> fluidTank: fluidtanks)
        {
            if (fluidTank.getRight().isMouseInside(mouseX - x, mouseY - y))
            {
                final List<String> lines = new ArrayList<>();
                if (fluidTank.getLeft().getFluid() == null || fluidTank.getLeft().getFluidAmount() == 0)
                    lines.add("Empty");
                else
                {
                    lines.add(TextFormatting.GOLD + fluidTank.getLeft().getFluid().getLocalizedName());

                    if (fluidTank.getMiddle() != null)
                        lines.addAll(fluidTank.getMiddle().get(fluidTank.getLeft()));

                    lines.add(TextFormatting.GOLD + "" + fluidTank.getLeft().getFluidAmount() + " / "
                            + fluidTank.getLeft().getCapacity() + " mB");
                }
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        for (Pair<ISteamTank, GuiSpace> steamTank: steamtanks)
        {
            if (steamTank.getValue().isMouseInside(mouseX - x, mouseY - y))
            {
                final List<String> lines = new ArrayList<>();
                if (steamTank.getKey().getSteam() == 0)
                    lines.add("Empty");
                else if (steamTank.getKey().getSteam() / steamTank.getKey().getActualCapacity() < 1)
                    lines.add(TextFormatting.GOLD + "" + steamTank.getKey().getSteam() + " / "
                            + steamTank.getKey().getActualCapacity());
                else
                {
                    lines.add(
                            (this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                                    + "" + steamTank.getKey().getSteam() + " / " +
                                    steamTank.getKey().getActualCapacity());
                    lines.add(
                            (this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                                    + "Overload!");
                }
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        for (Pair<MultiFluidTank, GuiSpace> multiFluidTank: multiFluidTanks)
        {
            if (multiFluidTank.getValue().isMouseInside(mouseX - x, mouseY - y))
            {
                final List<String> lines = new ArrayList<>();
                if (multiFluidTank.getKey().getFluids().isEmpty())
                    lines.add("Empty");
                else
                {
                    for (FluidStack fluidStack: multiFluidTank.getKey().getFluids())
                    {
                        lines.add(TextFormatting.GOLD + fluidStack.getLocalizedName());
                        lines.add(TextFormatting.GOLD + "" + fluidStack.amount + " mB");
                    }
                    lines.add(TextFormatting.GOLD + "Capacity: " + multiFluidTank.getKey().getCapacity());
                }
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        for (Pair<GuiSpace, Supplier<List<String>>> tooltip: this.tooltips)
        {
            if (mouseX > x + tooltip.getKey().getX() && mouseX < x + tooltip.getKey().getEndX() &&
                    mouseY > y + tooltip.getKey().getY() && mouseY < y + tooltip.getKey().getEndY())
            {
                GuiUtils.drawHoveringText(tooltip.getValue().get(),
                        mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);

        for (Pair<Vector2i, String> label: this.labels)
        {
            this.fontRenderer.drawString(label.getValue(), label.getKey().getX(), label.getKey().getY(), 4210752);
        }
        for (Pair<GuiSpace, ItemStack> itemStack: this.itemStacks)
        {
            ((GuiHelper) BrokkGuiPlatform.getInstance().getGuiHelper()).drawItemStack(this.renderer,
                    itemStack.getKey().getX() + itemStack.getKey().getWidth() / 2,
                    itemStack.getKey().getY() + itemStack.getKey().getHeight() / 2, itemStack.getKey().getWidth(),
                    itemStack.getKey().getHeight(), 0, itemStack.getValue(), Color.WHITE);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        this.mc.renderEngine.bindTexture(background);
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        for (Pair<Function<Integer, Integer>, GuiProgress> animatedSprites: animatedSprites)
        {
            GuiProgress progress = animatedSprites.getValue();

            int projectedLength = progress.getDirection().isVertical() ? progress.getSpace().getHeight() : progress
                    .getSpace().getWidth();

            int originalLength = progress.getDirection().isVertical() ? progress.getSpace().getT() - progress
                    .getSpace().getV() : progress.getSpace().getS() - progress.getSpace().getU();

            int projectedCurrent = animatedSprites.getKey().apply(projectedLength);

            int xStart = x + progress.getSpace().getX();
            int yStart = y + progress.getSpace().getY();

            int width = progress.getDirection().isVertical() ? progress.getSpace().getWidth() : projectedCurrent;
            int height = progress.getDirection().isVertical() ? projectedCurrent : progress.getSpace().getHeight();

            int u = progress.getSpace().getU() + progress.getPaddingLeft();
            int v = progress.getSpace().getV() + progress.getPaddingBottom();
            int s = progress.getSpace().getS() - progress.getPaddingRight();
            int t = progress.getSpace().getT() - progress.getPaddingTop();

            int originalCurrent = animatedSprites.getKey().apply(originalLength);

            if (progress.getDirection().isPositive())
            {
                if (!progress.getDirection().isVertical())
                {
                    // TODO
                }
                else
                {
                    v -= originalCurrent;
                    yStart -= projectedCurrent;
                }
            }
            else
            {
                if (!progress.getDirection().isVertical() && !progress.isRevert())
                    u = s - originalCurrent;
                else
                {
                    // TODO
                }
            }

            this.drawTexturedModalRect(xStart, yStart, u, v, width, height);
        }

        for (Triple<IFluidTank, ITankTooltipSupplier, GuiSpace> fluidTank: fluidtanks)
        {
            if (fluidTank.getLeft().getFluid() != null)
                this.drawFluid(fluidTank.getLeft().getFluid(), x + fluidTank.getRight().getX(),
                        y + fluidTank.getRight().getY(), fluidTank.getRight().getWidth(),
                        fluidTank.getRight().getHeight(), fluidTank.getLeft().getCapacity());
        }

        for (Pair<ISteamTank, GuiSpace> steamTank: steamtanks)
        {
            if (steamTank.getKey().getSteam() != 0 && steamTank.getKey().getCapacity() != 0)
                this.drawFluid(steamTank.getKey().toFluidStack(), x + steamTank.getValue().getX(),
                        y + steamTank.getValue().getY(), steamTank.getValue().getWidth(),
                        steamTank.getValue().getHeight(), steamTank.getKey().getActualCapacity());
        }

        for (Pair<MultiFluidTank, GuiSpace> multiFluidTank: multiFluidTanks)
        {
            if (!multiFluidTank.getKey().getFluids().isEmpty())
            {
                int pixels = 0;
                for (FluidStack fluidStack: multiFluidTank.getKey().getFluids())
                {
                    int fluidHeight = (int) ((float) fluidStack.amount / multiFluidTank.getKey().getCapacity() *
                            multiFluidTank.getValue().getHeight());
                    pixels += fluidHeight;
                    this.drawFluid(fluidStack, x + multiFluidTank.getValue().getX(),
                            y + multiFluidTank.getValue().getY() + multiFluidTank.getValue().getHeight() - pixels,
                            multiFluidTank.getValue().getWidth(), fluidHeight);
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0)
        {
            this.buttons.forEach(button ->
            {
                if (button.getKey().isMouseInside(mouseX - this.guiLeft, mouseY - this.guiTop))
                {
                    button.getValue().run();
                    this.mc.getSoundHandler().playSound(
                            PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            });
        }
    }

    protected void drawFluid(final FluidStack fluid, final int x, final int y, final int width, final int height,
                             final int maxCapacity)
    {
        int offsetHeight = (int) (fluid.amount > maxCapacity ? height : (fluid.amount / (maxCapacity * 1F) * height));
        this.drawFluid(fluid, x, y + height - offsetHeight, width, offsetHeight);
    }

    protected void drawFluid(FluidStack fluid, int x, int y, int width, int height)
    {
        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        final ResourceLocation still = fluid.getFluid().getStill(fluid);
        final TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(still.toString());

        final int iconHeight = sprite.getIconHeight();
        int offsetHeight = height;

        int iteration = 0;
        while (offsetHeight != 0)
        {
            final int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
            this.drawTexturedModalRect(x, y + height - offsetHeight, sprite, width, curHeight);
            offsetHeight -= curHeight;
            iteration++;
            if (iteration > 50)
                break;
        }
    }

    public T getMachine()
    {
        return this.machine;
    }
}
