package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.client.gui.util.GuiProgress;
import net.ros.client.gui.util.GuiSpace;
import net.ros.client.gui.util.GuiTexturedSpace;
import net.ros.common.ROSConstants;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.recipe.Materials;
import net.ros.common.tile.machine.TileAlloyCauldron;

import java.util.Collections;

public class GuiAlloyCauldron extends GuiMachineBase<TileAlloyCauldron>
{
    public static final ResourceLocation BACKGROUND =
            new ResourceLocation(ROSConstants.MODID, "textures/gui/alloycauldron.png");

    public GuiAlloyCauldron(final EntityPlayer player, final TileAlloyCauldron alloyCauldron)
    {
        super(player, alloyCauldron, BACKGROUND);

        this.xSize = 202;
        this.ySize = 195;

        this.addAnimatedSprite(this.getMachine()::getBurnTimeScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(18).y(70).width(13).height(13).u(243).v(12)
                        .s(243 + 13).t(25).build()).direction(GuiProgress.StartDirection.TOP).revert(false).build());
        this.addAnimatedSprite(this.getMachine()::getHeatScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(4).y(94).width(12).height(71).u(202).v(102)
                        .s(202 + 12).t(102 + 71).build()).direction(GuiProgress.StartDirection.TOP).revert(false)
                        .build());
        this.addAnimatedSprite(this.getMachine()::getMeltProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(37).y(38).width(25).height(16).u(202).v(14).s
                        (202 + 24).t(14 + 17).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());
        this.addAnimatedSprite(this.getMachine()::getCastProgressScaled,
                GuiProgress.builder().space(GuiTexturedSpace.builder().x(135).y(52).width(25).height(16).u(202).v(14).s
                        (202 + 24).t(14 + 17).build()).direction(GuiProgress.StartDirection.RIGHT).revert(true)
                        .build());

        this.addTooltip(new GuiSpace(4, 21, 12, 71), () ->
                Collections.singletonList(this.getHeatTooltip(this.getMachine()::getHeat,
                        this.getMachine()::getMaxHeat)));

        this.addTooltip(new GuiSpace(61, 97, 18, 12), () ->
                Collections.singletonList("To exit tank"));
        this.addTooltip(new GuiSpace(80, 97, 18, 12), () ->
                Collections.singletonList("To exit tank"));
        this.addTooltip(new GuiSpace(105, 97, 18, 12), () ->
                Collections.singletonList(TextFormatting.RED + "Void tank"));

        this.addFluidTank(this.getMachine().getInputTankLeft(), 61, 24, 18, 73);
        this.addFluidTank(this.getMachine().getInputTankRight(), 80, 24, 18, 73);
        this.addFluidTank(this.getMachine().getOutputTank(), 105, 24, 18, 73);

        this.addSimpleButton(new GuiSpace(61, 97, 18, 12), () ->
                new ServerActionBuilder("LEFT_TANK_VOID").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(80, 97, 18, 12), () ->
                new ServerActionBuilder("RIGHT_TANK_VOID").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(105, 97, 18, 12), () ->
                new ServerActionBuilder("OUTPUT_TANK_VOID").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(61, 6, 37, 18), () ->
                new ServerActionBuilder("ALLOY").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(127, 69, 13, 13), () ->
                new ServerActionBuilder("CAST_INGOT").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(141, 69, 13, 13), () ->
                new ServerActionBuilder("CAST_PLATE").toTile(this.getMachine()).send());
        this.addSimpleButton(new GuiSpace(155, 69, 13, 13), () ->
                new ServerActionBuilder("CAST_BLOCK").toTile(this.getMachine()).send());

        this.addLabel(65, 12, "ALLOY");

        this.addItemStack(new GuiSpace(127, 69, 13, 13),
                Materials.getIngotFromMetal(Materials.IRON));
        this.addItemStack(new GuiSpace(141, 69, 13, 13),
                Materials.getPlateFromMetal(Materials.IRON));
        this.addItemStack(new GuiSpace(155, 69, 13, 13),
                Materials.getBlockFromMetal(Materials.IRON));
    }
}
