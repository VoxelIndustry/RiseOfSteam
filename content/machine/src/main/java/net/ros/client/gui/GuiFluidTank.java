package net.ros.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.common.ROSConstants;
import net.ros.common.machine.module.impl.FluidStorageModule;
import net.ros.common.tile.machine.TileTank;

public class GuiFluidTank extends GuiMachineBase<TileTank>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ROSConstants.MODID,
            "textures/gui/fluidtank.png");

    public GuiFluidTank(final EntityPlayer player, final TileTank fluidtank)
    {
        super(player, fluidtank, BACKGROUND);

        this.addFluidTank((IFluidTank) fluidtank.getModule(FluidStorageModule.class).getFluidHandler("fluid"),
                78, 7, 18, 73);
    }
}
