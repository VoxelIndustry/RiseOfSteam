package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.tile.TileEngineerStorage;

public class GuiEngineerStorage extends GuiMachineBase<TileEngineerStorage>
{
    public static ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID,"textures/gui/engineerstorage.png");

    public GuiEngineerStorage(EntityPlayer player, TileEngineerStorage tile)
    {
        super(player, tile, BACKGROUND);
    }
}
