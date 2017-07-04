package net.qbar.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.steam.ISteamTank;
import net.qbar.common.tile.TileInventoryBase;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiMachineBase<T extends TileInventoryBase & IContainerProvider> extends GuiContainer
{
    private T machine;

    private final List<Pair<IFluidTank, GuiSpace>> fluidtanks;
    private final List<Pair<ISteamTank, GuiSpace>> steamtanks;

    public GuiMachineBase(EntityPlayer player, T tile)
    {
        super(tile.createContainer(player));

        this.machine = tile;
        this.fluidtanks = new ArrayList<>();
        this.steamtanks = new ArrayList<>();
    }

    protected void addFluidTank(IFluidTank fluidTank, int x, int y, int width, int height)
    {
        this.fluidtanks.add(Pair.of(fluidTank, new GuiSpace(x, y, width, height)));
    }

    protected void addSteamTank(ISteamTank steamTank, int x, int y, int width, int height)
    {
        this.steamtanks.add(Pair.of(steamTank, new GuiSpace(x, y, width, height)));
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.translate(-this.guiLeft, -this.guiTop, 0.0F);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        for (Pair<IFluidTank, GuiSpace> fluidTank : fluidtanks)
        {
            if (fluidTank.getValue().isMouseInside(mouseX - x, mouseY - y))
            {
                final List<String> lines = new ArrayList<>();
                if (fluidTank.getKey().getFluid() == null || fluidTank.getKey().getFluidAmount() == 0)
                    lines.add("Empty");
                else
                {
                    lines.add(TextFormatting.GOLD + fluidTank.getKey().getFluid().getLocalizedName());
                    lines.add(TextFormatting.GOLD + "" + fluidTank.getKey().getFluidAmount() + " / "
                            + fluidTank.getKey().getCapacity() + " mB");
                }
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        for (Pair<ISteamTank, GuiSpace> steamTank : steamtanks)
        {
            if (steamTank.getValue().isMouseInside(mouseX - x, mouseY - y))
            {
                final List<String> lines = new ArrayList<>();
                if (steamTank.getKey().getSteam() == 0)
                    lines.add("Empty");
                else if (steamTank.getKey().getSteam() / steamTank.getKey().getCapacity() < 1)
                    lines.add(TextFormatting.GOLD + "" + steamTank.getKey().getSteam() + " / "
                            + steamTank.getKey().getCapacity());
                else
                {
                    lines.add(
                            (this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                                    + "" + steamTank.getKey().getSteam() + " / " + steamTank.getKey().getCapacity());
                    lines.add(
                            (this.mc.world.getTotalWorldTime() / 10 % 2 == 0 ? TextFormatting.RED : TextFormatting.GOLD)
                                    + "Overload!");
                }
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
            }
        }
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0F);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        for (Pair<IFluidTank, GuiSpace> fluidTank : fluidtanks)
        {
            if (fluidTank.getKey().getFluid() != null)
                this.drawFluid(fluidTank.getKey().getFluid(), x + fluidTank.getValue().getX(),
                        y + fluidTank.getValue().getY(), fluidTank.getValue().getWidth(),
                        fluidTank.getValue().getHeight(), fluidTank.getKey().getCapacity());
        }

        for (Pair<ISteamTank, GuiSpace> steamTank : steamtanks)
        {
            if (steamTank.getKey().getSteam() != 0)
                this.drawFluid(steamTank.getKey().toFluidStack(), x + steamTank.getValue().getX(),
                        y + steamTank.getValue().getY(), steamTank.getValue().getWidth(),
                        steamTank.getValue().getHeight(),
                        (int) (steamTank.getKey().getCapacity() * steamTank.getKey().getMaxPressure()));
        }
    }

    protected void drawFluid(final FluidStack fluid, final int x, final int y, final int width, final int height,
                             final int maxCapacity)
    {
        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        final ResourceLocation still = fluid.getFluid().getStill(fluid);
        final TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(still.toString());

        final int drawHeight = (int) (fluid.amount / (maxCapacity * 1F) * height);
        final int iconHeight = sprite.getIconHeight();
        int offsetHeight = drawHeight;

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
