package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.common.tile.machine.TileSortingMachine;

public class GuiSortingMachine extends GuiMachineBase<TileSortingMachine>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/sortingmachine.png");

    public GuiSortingMachine(final EntityPlayer player, final TileSortingMachine sortingMachine)
    {
        super(player, sortingMachine);

        this.addSteamTank(sortingMachine.getSteamTank(), 151,7,18,73);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {
        this.mc.renderEngine.bindTexture(GuiSortingMachine.BACKGROUND);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        final int j = this.getMachine().getProgressScaled(24);
        if (j > 0)
            this.drawTexturedModalRect(x + 79, y + 34, 176, 14, j + 1, 16);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}
