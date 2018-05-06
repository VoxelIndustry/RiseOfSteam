package net.ros.common.gui;

public enum LogisticGui implements IGuiReference
{
    EXTRACTOR, SPLITTER;

    private final boolean containerBuilder;

    LogisticGui(final boolean containerBuilder)
    {
        this.containerBuilder = containerBuilder;
    }

    LogisticGui()
    {
        this(true);
    }

    @Override
    public boolean useContainerBuilder()
    {
        return this.containerBuilder;
    }

    @Override
    public int getUniqueID()
    {
        return this.ordinal();
    }
}
