package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileTank;

public class GuiFluidTank extends GuiMachineBase<TileTank>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/fluidtank.png");

    public GuiFluidTank(final EntityPlayer player, final TileTank fluidtank)
    {
        super(player, fluidtank);

        this.addFluidTank(fluidtank.getTank(), 78, 7, 18, 73);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiFluidTank.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}
