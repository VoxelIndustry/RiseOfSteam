package net.ros.common.gui;

import net.ros.client.gui.GuiExtractor;
import net.ros.client.gui.GuiSplitter;
import net.ros.client.gui.GuiSteamVent;

public class LogisticGui
{
    public static final GuiReference EXTRACTOR = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiExtractor::new));

    public static final GuiReference SPLITTER = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiSplitter::new));

    public static final GuiReference STEAM_VENT = new GuiReference(GuiManager::getContainer,
            GuiManager.getBrokkGuiContainer(GuiSteamVent::new));
}
