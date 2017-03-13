package net.qbar.common.multiblock;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.qbar.common.init.QBarItems;

public class Blueprints
{
    private static volatile Blueprints instance = null;

    public static final Blueprints getInstance()
    {
        if (Blueprints.instance == null)
            synchronized (Blueprints.class)
            {
                if (Blueprints.instance == null)
                    Blueprints.instance = new Blueprints();
            }
        return Blueprints.instance;
    }

    private final HashMap<String, Blueprint> blueprints;

    private Blueprints()
    {
        this.blueprints = new HashMap<>();

        this.registerBlueprint("fluidtank", Multiblocks.FLUID_TANK);
        this.registerBlueprint("mediumfluidtank", Multiblocks.MEDIUM_FLUID_TANK);
        this.registerBlueprint("bigfluidtank", Multiblocks.BIG_FLUID_TANK);

        this.registerBlueprint("boiler", Multiblocks.SOLID_BOILER);
        this.registerBlueprint("assembler", Multiblocks.ASSEMBLER);
        this.registerBlueprint("solarboiler", Multiblocks.SOLAR_BOILER);
        this.registerBlueprint("keypunch", Multiblocks.KEYPUNCH);
        this.registerBlueprint("liquidfuelboiler", Multiblocks.LIQUID_FUEL_BOILER);
        this.registerBlueprint("rollingmill", Multiblocks.ROLLING_MILL);
        this.registerBlueprint("steamfurnacemk1", Multiblocks.STEAM_FURNACE_MK1);
        this.registerBlueprint("steamfurnacemk2", Multiblocks.STEAM_FURNACE_MK2);
    }

    public void registerBlueprint(final String name, final IMultiblockDescriptor multiblock)
    {
        this.blueprints.put(name, new Blueprint(name, multiblock, multiblock.getBlockCount() * 4));
    }

    public HashMap<String, Blueprint> getBlueprints()
    {
        return this.blueprints;
    }

    public static final class Blueprint
    {
        private final String                name;
        private final IMultiblockDescriptor multiblock;
        private final int                   rodAmount;
        private final ItemStack             rodStack;

        public Blueprint(final String name, final IMultiblockDescriptor multiblock, final int rodAmount)
        {
            this.name = name;
            this.multiblock = multiblock;
            this.rodAmount = rodAmount;

            this.rodStack = new ItemStack(QBarItems.IRON_ROD, this.getRodAmount());
        }

        public String getName()
        {
            return this.name;
        }

        public IMultiblockDescriptor getMultiblock()
        {
            return this.multiblock;
        }

        public int getRodAmount()
        {
            return this.rodAmount;
        }

        public ItemStack getRodStack()
        {
            return this.rodStack;
        }
    }
}
