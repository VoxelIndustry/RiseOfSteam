package net.qbar.client.gui;

import org.yggard.brokkgui.wrapper.container.BrokkGuiContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.tile.TileKeypunch;

public class GuiKeypunch extends BrokkGuiContainer<BuiltContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/keypunch.png");

    private final TileKeypunch            keypunch;

    public GuiKeypunch(final EntityPlayer player, final TileKeypunch keypunch)
    {
        super(keypunch.createContainer(player));

        this.keypunch = keypunch;
    }
}
