package net.ros.common.gui;

import fr.ourten.teabeans.function.TriFunction;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.client.gui.util.GuiMachineBase;
import net.ros.common.ROSConstants;
import net.ros.common.container.IContainerProvider;
import net.ros.common.tile.TileBase;
import net.voxelindustry.brokkgui.wrapper.container.BrokkGuiContainer;
import net.voxelindustry.brokkgui.wrapper.impl.BrokkGuiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class GuiManager
{
    private static final List<GuiReference> guiList = new ArrayList<>();

    static int getNextID(GuiReference gui)
    {
        guiList.add(gui);
        return guiList.size() - 1;
    }

    public static GuiReference getGui(int ID)
    {
        return guiList.get(ID);
    }

    public static Container getContainer(EntityPlayer player, World w, BlockPos pos)
    {
        TileEntity tile = w.getTileEntity(pos);

        if (tile instanceof IContainerProvider)
            return ((IContainerProvider) tile).createContainer(player);
        return null;
    }

    public static <T extends TileEntity> TriFunction<EntityPlayer, World, BlockPos, Gui> getBrokkGuiContainer(BiFunction<EntityPlayer,
            T, BrokkGuiContainer<? extends Container>> guiConstructor)
    {
        return (player, w, pos) -> BrokkGuiManager.getBrokkGuiContainer(ROSConstants.MODID,
                guiConstructor.apply(player, (T) w.getTileEntity(pos)));
    }

    public static <T extends TileBase & IContainerProvider> TriFunction<EntityPlayer, World, BlockPos, Gui> getGuiContainer(BiFunction<EntityPlayer,
            T, GuiMachineBase<T>> guiConstructor)
    {
        return (player, w, pos) -> guiConstructor.apply(player, (T) w.getTileEntity(pos));
    }
}
