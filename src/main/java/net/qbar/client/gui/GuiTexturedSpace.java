package net.qbar.client.gui;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GuiTexturedSpace
{
    private final int x, y, width, height;
    private int u,v,s,t;
}
