package net.ros.common.gui;

public enum MachineGui implements IGuiReference
{
    BOILER, KEYPUNCH, ROLLINGMILL, FLUIDTANK, STEAMFURNACE, ASSEMBLER, LIQUIDBOILER, SOLARBOILER,
    STEAMFURNACEMK2, OREWASHER, SORTINGMACHINE, TINYMININGDRILL, SMALLMININGDRILL, SAWMILL, ENGINEERSTORAGE,
    ENGINEERWORKBENCH, BLUEPRINTPRINTER, CRAFTCARDLIBRARY, ALLOYCAULDRON, CAPSULE_FILLER, STEAMTANK;

    private final boolean containerBuilder;

    MachineGui(final boolean containerBuilder)
    {
        this.containerBuilder = containerBuilder;
    }

    MachineGui()
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
        return this.ordinal() + 100;
    }
}
