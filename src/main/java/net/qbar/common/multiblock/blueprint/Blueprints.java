package net.qbar.common.multiblock.blueprint;

import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.qbar.common.multiblock.IMultiblockDescriptor;
import net.qbar.common.multiblock.Multiblocks;

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

        this.registerBlueprint("fluidtank", Multiblocks.FLUID_TANK)
                .addStep(60, new ItemStack(Blocks.DIRT, 40), new ItemStack(Blocks.IRON_BLOCK, 10))
                .addStep(20, new ItemStack(Items.BLAZE_ROD, 8));

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

    public Blueprint registerBlueprint(final String name, final IMultiblockDescriptor multiblock)
    {
        this.blueprints.put(name, new Blueprint(name, multiblock, multiblock.getBlockCount() * 4));
        return this.blueprints.get(name);
    }

    public HashMap<String, Blueprint> getBlueprints()
    {
        return this.blueprints;
    }

    public Blueprint getBlueprint(final String name)
    {
        return this.blueprints.get(name);
    }
}
