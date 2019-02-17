package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiSpace;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.tile.machine.TileTinyMiningDrill;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;

import java.text.NumberFormat;
import java.util.Collections;

public class GuiTinyMiningDrill extends GuiMachineBase<TileTinyMiningDrill>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/tinyminingdrill" + ".png");

    private TileTinyMiningDrill miningDrill;

    public GuiTinyMiningDrill(final EntityPlayer player, final TileTinyMiningDrill miningdrill)
    {
        super(player, miningdrill, BACKGROUND);

        this.miningDrill = miningdrill;

        this.addAnimatedSprite(this::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder()
                        .x(61).y(39).width(55).height(7)
                        .u(176).v(0).s(176 + 55).t(7).build())
                        .direction(GuiProgress.StartDirection.RIGHT).revert(true).build());

        this.addTooltip(new GuiSpace(61, 39, 55, 7), () ->
                Collections.singletonList(String.valueOf(NumberFormat.getPercentInstance()
                        .format(this.getMachine().getProgress()))));

        this.addSimpleButton(new GuiSpace(72, 25, 31, 12), () ->
        {
            if (!this.miningDrill.isDoStart())
            {
                new ServerActionBuilder("START").toTile(miningdrill).send();
                this.miningDrill.setDoStart(true);
            }
        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        if (this.miningDrill.isDoStart() ||
                !this.miningDrill.getModule(InventoryModule.class).getInventory("basic").getStackInSlot(0).isEmpty())
            this.drawTexturedModalRect(x + 72, y + 25, 176, 7, 31, 12);
        this.fontRenderer.drawString("START", x + 73, y + 27, 4210752);
    }

    private int getProgressScaled(final int pixels)
    {
        return (int) (pixels * this.getMachine().getProgress());
    }
}
