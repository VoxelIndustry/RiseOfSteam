package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.machine.TileCapsuleFiller;

public class GuiCapsuleFiller extends GuiMachineBase<TileCapsuleFiller>
{
    private static ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/capsulefiller.png");

    public GuiCapsuleFiller(EntityPlayer player, TileCapsuleFiller tile)
    {
        super(player, tile, BACKGROUND);

        this.addSteamTank(tile.getTank(), 151, 7, 18, 73);
    }
}
