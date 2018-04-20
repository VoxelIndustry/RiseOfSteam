package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.CraftingModule;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.tile.machine.TileOreWasher;

public class GuiOreWasher extends GuiMachineBase<TileOreWasher>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/orewasher.png");

    public GuiOreWasher(final EntityPlayer player, final TileOreWasher orewasher)
    {
        super(player, orewasher, BACKGROUND);

        this.addFluidTank((IFluidTank) orewasher.getModule(FluidStorageModule.class).getFluidHandler("washer"),
                7, 7, 18, 73);

        this.addSteamTank(orewasher.getModule(SteamModule.class)
                .getInternalSteamHandler(), 151, 7, 18, 73);
        this.addAnimatedSprite(orewasher.getModule(CraftingModule.class)::getProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
    }
}
