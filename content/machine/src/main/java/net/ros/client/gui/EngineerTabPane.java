package net.ros.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.gui.MachineGui;
import net.ros.common.network.OpenGuiPacket;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import net.voxelindustry.brokkgui.wrapper.elements.ItemStackView;
import net.voxelindustry.steamlayer.network.action.ServerActionBuilder;
import net.voxelindustry.steamlayer.tile.TileBase;

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
                return MachineGui.ENGINEER_WORKBENCH.getUniqueID();
            case CARDLIBRARY:
                return MachineGui.CRAFT_CARD_LIBRARY.getUniqueID();
            case PRINTER:
                return MachineGui.BLUEPRINT_PRINTER.getUniqueID();
            case STORAGE:
                return MachineGui.ENGINEER_STORAGE.getUniqueID();
        }
        return 0;
    }
}
