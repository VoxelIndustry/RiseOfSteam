package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.QBar;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.tile.machine.TileTank;

public class GuiFluidTank extends GuiMachineBase<TileTank>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBar.MODID, "textures/gui/fluidtank.png");

    public GuiFluidTank(final EntityPlayer player, final TileTank fluidtank)
    {
        super(player, fluidtank, BACKGROUND);

        this.addFluidTank(fluidtank.getTank(), 78, 7, 18, 73);
    }
}
