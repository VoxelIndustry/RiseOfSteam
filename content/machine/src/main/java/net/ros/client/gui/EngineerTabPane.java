package net.ros.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.gui.MachineGui;
import net.ros.common.network.OpenGuiPacket;
import net.ros.common.network.action.ServerActionBuilder;
import net.ros.common.tile.TileBase;
import org.yggard.brokkgui.element.GuiButton;
import org.yggard.brokkgui.panel.GuiAbsolutePane;
import org.yggard.brokkgui.wrapper.elements.ItemStackView;

class EngineerTabPane extends GuiAbsolutePane
{
    private final WorkshopMachine machine;

    EngineerTabPane(TileBase tile, WorkshopMachine machine)
    {
        this.machine = machine;

        this.setWidth(23);
        this.setHeight(5 + WorkshopMachine.VALUES.length * 24);

        new ServerActionBuilder("MACHINES_LOAD").toTile(tile).then(response ->
        {
            int index = 0;
            for (WorkshopMachine type : WorkshopMachine.VALUES)
            {
                if (response.hasKey(type.name()))
                {
                    this.addOnglet(type, index, BlockPos.fromLong(response.getLong(type.name())));
                    index++;
                }
            }
        }).send();
    }

    private void addOnglet(WorkshopMachine type, int index, BlockPos pos)
    {
        GuiButton button = new GuiButton();
        button.setWidth(26);
        button.setHeight(23);
        button.setDisabled(type == machine);
        button.getStyleClass().add("tab");

        button.setOnActionEvent(e ->
                new OpenGuiPacket(Minecraft.getMinecraft().world, pos, this.getMachineGui(type)).sendToServer());

        ItemStackView view = new ItemStackView(new ItemStack(type.getBlock()));
        view.setWidth(18);
        view.setHeight(18);
        view.setItemTooltip(true);

        this.addChild(button, 0, 5 + (index * 24));
        this.addChild(view, 5, 7 + (index * 24));
    }

    private int getMachineGui(WorkshopMachine type)
    {
        switch (type)
        {
            case KEYPUNCH:
                return MachineGui.KEYPUNCH.getUniqueID();
            case WORKBENCH:
                return MachineGui.ENGINEERWORKBENCH.getUniqueID();
            case CARDLIBRARY:
                return MachineGui.CRAFTCARDLIBRARY.getUniqueID();
            case PRINTER:
                return MachineGui.BLUEPRINTPRINTER.getUniqueID();
            case STORAGE:
                return MachineGui.ENGINEERSTORAGE.getUniqueID();
        }
        return 0;
    }
}
