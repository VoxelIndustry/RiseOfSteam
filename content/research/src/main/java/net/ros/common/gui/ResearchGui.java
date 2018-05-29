package net.ros.common.gui;

public enum ResearchGui implements IGuiReference
{
    RESEARCH_BOOK;

    private final boolean containerBuilder;

    ResearchGui(final boolean containerBuilder) {
        this.containerBuilder = containerBuilder;
    }

    ResearchGui() {
        this(true);
    }

    @Override
    public boolean useContainerBuilder() {
        return this.containerBuilder;
    }

    @Override
    public int getUniqueID() {
        return this.ordinal() + 200;
    }
}
