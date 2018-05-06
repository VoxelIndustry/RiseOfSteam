package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiSpace;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.machine.module.impl.SteamModule;
import net.ros.common.tile.machine.TileSmallMiningDrill;

import java.util.Collections;

public class GuiSmallMiningDrill extends GuiMachineBase<TileSmallMiningDrill>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/smallminingdrill" + ".png");

    public GuiSmallMiningDrill(final EntityPlayer player, final TileSmallMiningDrill miningdrill)
    {
        super(player, miningdrill, BACKGROUND);

        this.addFluidTank((IFluidTank) miningdrill.getModule(FluidStorageModule.class).getFluidHandler("water"),
                128, 7, 18, 73);
        this.addSteamTank(miningdrill.getModule(SteamModule.class).getInternalSteamHandler(),
                151, 7, 18, 73);

        this.addAnimatedSprite(this::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(10).y(79).width(12).height(71).u(176).v(84)
                        .s(176 + 12).t(85 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());

        this.addTooltip(new GuiSpace(10, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(this.getMachine()::getHeat, this.getMachine()
                        ::getMaxHeat)));
    }

    private int getHeatScaled(final int pixels)
    {
        final int i = (int) this.getMachine().getMaxHeat();

        if (i == 0)
            return 0;

        return (int) Math.min(this.getMachine().getHeat() * pixels / i, pixels);
    }
}
