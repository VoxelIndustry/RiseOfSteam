package net.qbar.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.qbar.client.gui.util.GuiMachineBase;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.module.impl.FluidStorageModule;
import net.qbar.common.tile.machine.TileTank;

public class GuiFluidTank extends GuiMachineBase<TileTank>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(QBarConstants.MODID,
            "textures/gui/fluidtank.png");

    public GuiFluidTank(final EntityPlayer player, final TileTank fluidtank)
    {
        super(player, fluidtank, BACKGROUND);

        this.addFluidTank((IFluidTank) fluidtank.getModule(FluidStorageModule.class).getFluidHandler("fluid"),
                78, 7, 18, 73);
    }
}
