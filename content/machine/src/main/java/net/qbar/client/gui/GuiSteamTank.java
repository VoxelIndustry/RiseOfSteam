package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.tile.machine.TileSteamTank;

public class GuiSteamTank extends GuiMachineBase<TileSteamTank>
{
    private static ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/steamtank.png");

    public GuiSteamTank(EntityPlayer player, TileSteamTank tile)
    {
        super(player, tile, BACKGROUND);

        this.addFluidTank((IFluidTank) tile.getModule(FluidStorageModule.class).getFluidHandler("water"),
                88, 6, 18, 73);
        this.addSteamTank(tile.getModule(SteamModule.class).getInternalSteamHandler(), 70, 6, 18, 73);

        this.addLabel(5, 4, tile.getDisplayName().getFormattedText());
    }
}
