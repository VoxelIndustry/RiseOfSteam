package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.tile.machine.TileCapsuleFiller;

public class GuiCapsuleFiller extends GuiMachineBase<TileCapsuleFiller>
{
    private static ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/capsulefiller.png");

    public GuiCapsuleFiller(EntityPlayer player, TileCapsuleFiller tile)
    {
        super(player, tile, BACKGROUND);

        this.addSteamTank(tile.getModule(SteamModule.class).getInternalSteamHandler(), 151, 7, 18, 73);
    }
}
