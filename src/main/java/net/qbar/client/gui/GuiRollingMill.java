package net.qbar.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;

public class GuiRollingMill extends GuiContainer
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/rollingmill.png");

    public GuiRollingMill(final Container inventorySlotsIn)
    {
        super(inventorySlotsIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY)
    {

    }
}
