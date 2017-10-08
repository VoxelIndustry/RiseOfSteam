package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.machine.TileEngineerStorage;

public class GuiEngineerStorage extends GuiMachineBase<TileEngineerStorage>
{
    public static ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/engineerstorage.png");

    public GuiEngineerStorage(EntityPlayer player, TileEngineerStorage tile)
    {
        super(player, tile, BACKGROUND);
    }
}
