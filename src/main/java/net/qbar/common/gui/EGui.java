package net.qbar.common.gui;

public enum EGui
{
    BOILER, EXTRACTOR, KEYPUNCH, SPLITTER, ROLLINGMILL, FLUIDTANK, STEAMFURNACE, ASSEMBLER, LIQUIDBOILER, SOLARBOILER,
    STEAMFURNACEMK2, OREWASHER, SORTINGMACHINE, TINYMININGDRILL, SMALLMININGDRILL, SAWMILL, ENGINEERSTORAGE,
    ENGINEERWORKBENCH, BLUEPRINTPRINTER, CRAFTCARDLIBRARY;

    private final boolean containerBuilder;

    EGui(final boolean containerBuilder)
    {
        this.containerBuilder = containerBuilder;
    }

    EGui()
    {
        this(true);
    }

    public boolean useContainerBuilder()
    {
        return this.containerBuilder;
    }
}
