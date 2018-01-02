package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.client.gui.util.GuiProgress;
import net.qbar.client.gui.util.GuiSpace;
import net.qbar.client.gui.util.GuiTexturedSpace;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.machine.TileAlloyCauldron;

import java.util.Collections;

public class GuiAlloyCauldron extends GuiMachineBase<TileAlloyCauldron>
{
    public static final ResourceLocation BACKGROUND =
            new ResourceLocation(QBarConstants.MODID, "textures/gui/alloycauldron.png");

    public GuiAlloyCauldron(final EntityPlayer player, final TileAlloyCauldron alloyCauldron)
    {
        super(player, alloyCauldron, BACKGROUND);

        this.addAnimatedSprite(this.getMachine()::getBurnTimeScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(81).y(38).width(14).height(13).u(176).v(12)
                        .s(190).t(25).build()).direction(GuiProgress.StartDirection.TOP).revert(false).build());
        this.addAnimatedSprite(this.getMachine()::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(4).y(79).width(12).height(78).u(176).v(102)
                        .s(176 + 12).t(102 + 79).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());
        this.addAnimatedSprite(this.getMachine()::getMeltProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(79).y(34).width(25).height(16).u(176).v(14).s
                        (176 + 25).t(14 + 16).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());

        this.addMultiFluidTank(alloyCauldron.getTanks(), 61, 7, 36, 73);

        this.addTooltip(new GuiSpace(4, 8, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(this.getMachine()::getHeat,
                        this.getMachine()::getMaxHeat)));
    }
}
