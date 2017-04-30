package net.qbar.common.gui;

public enum EGui
{
    BOILER(true), EXTRACTOR(true), KEYPUNCH(true), SPLITTER(true), ROLLINGMILL(true), FLUIDTANK(true), STEAMFURNACE(
            true), ASSEMBLER(true), LIQUIDBOILER(
                    true), SOLARBOILER(true), STEAMFURNACEMK2(true), OREWASHER(true), SORTINGMACHINE(true);

    private final boolean containerBuilder;

    private EGui(final boolean containerBuilder)
    {
        this.containerBuilder = containerBuilder;
    }

    public boolean useContainerBuilder()
    {
        return this.containerBuilder;
    }
}
