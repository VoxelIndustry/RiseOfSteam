package net.qbar.client.gui;

import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.paint.Background;
import org.yggard.brokkgui.paint.Color;
import org.yggard.brokkgui.paint.Texture;
import org.yggard.brokkgui.panel.GuiRelativePane;
import org.yggard.brokkgui.skin.GuiButtonSkin;
import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.slot.ListenerSlot;
import net.qbar.common.tile.TileKeypunch;

public class GuiKeypunch extends BrokkGuiContainer<BuiltContainer>
{
    private static final int     xSize      = 176, ySize = 166;

    private static final Texture BACKGROUND = new Texture(QBar.MODID + ":textures/gui/keypunch.png", 0, 0,
            GuiKeypunch.xSize / 256.0f, GuiKeypunch.ySize / 256.0f);

    private final TileKeypunch   keypunch;

    private final GuiButton      assemble;

    public GuiKeypunch(final EntityPlayer player, final TileKeypunch keypunch)
    {
        super(keypunch.createContainer(player));
        this.setWidth(GuiKeypunch.xSize);
        this.setHeight(GuiKeypunch.ySize);
        this.setxRelativePos(0.5f);
        this.setyRelativePos(0.5f);

        this.keypunch = keypunch;

        final GuiRelativePane mainPanel = new GuiRelativePane();
        this.setMainPanel(mainPanel);

        mainPanel.setBackground(new Background(GuiKeypunch.BACKGROUND));

        this.assemble = new GuiButton("PRINT");
        this.assemble.setWidth(56);
        this.assemble.setHeight(16);

        ((GuiButtonSkin) this.assemble.getSkin())
                .setBackground(new Background(new Color(Integer.parseInt("03", 16) / 255.0f,
                        Integer.parseInt("A9", 16) / 255.0f, Integer.parseInt("F4", 16) / 255.0f)));
        ((GuiButtonSkin) this.assemble.getSkin())
                .setHoveredBackground(new Background(new Color(Integer.parseInt("4F", 16) / 255.0f,
                        Integer.parseInt("C3", 16) / 255.0f, Integer.parseInt("F7", 16) / 255.0f)));

        ((ListenerSlot) this.getContainer().getSlot(36)).setOnChange(stack ->
        {
            if (!stack.isEmpty())
            {
                if (!this.getMainPanel().getChildrens().contains(this.assemble))
                    ((GuiRelativePane) this.getMainPanel()).addChild(this.assemble, 0.5f, 0.415f);
                if (stack.getTagCompound() == null && !this.assemble.getText().equals("PRINT"))
                    this.assemble.setText("PRINT");
                else if (stack.getTagCompound() != null && !this.assemble.getText().equals("LOAD"))
                    this.assemble.setText("LOAD");
            }
            else if (this.getContainer().getSlot(36).getStack().isEmpty()
                    && this.getMainPanel().getChildrens().contains(this.assemble))
                this.getMainPanel().removeChild(this.assemble);
        });
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);
    }
}
