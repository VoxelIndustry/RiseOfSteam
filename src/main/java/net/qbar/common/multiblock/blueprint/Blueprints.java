package net.qbar.common.multiblock.blueprint;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.hjson.JsonValue;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
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
    private final Gson                       gson;

    private Blueprints()
    {
        this.gson = new Gson();
        this.blueprints = new HashMap<>();

        this.registerBlueprint("fluidtank_small", Multiblocks.SMALL_FLUID_TANK)
                .addStep(60, new ItemStack(Blocks.DIRT, 40), new ItemStack(Blocks.IRON_BLOCK, 10))
                .addStep(20, new ItemStack(Items.BLAZE_ROD, 8));

        this.registerBlueprint("fluidtank_medium", Multiblocks.MEDIUM_FLUID_TANK);
        this.registerBlueprint("fluidtank_big", Multiblocks.BIG_FLUID_TANK);

        this.registerBlueprint("boiler", Multiblocks.SOLID_BOILER);
        this.registerBlueprint("assembler", Multiblocks.ASSEMBLER);
        this.registerBlueprint("solar_boiler", Multiblocks.SOLAR_BOILER);
        this.registerBlueprint("keypunch", Multiblocks.KEYPUNCH);
        this.registerBlueprint("liquidfuel_boiler", Multiblocks.LIQUID_FUEL_BOILER);
        this.registerBlueprint("rollingmill", Multiblocks.ROLLING_MILL);
        this.registerBlueprint("steamfurnacemk1", Multiblocks.STEAM_FURNACE_MK1)
                .addStep(20, new ItemStack(Blocks.BRICK_BLOCK, 4)).addStep(10, new ItemStack(QBarItems.IRON_ROD, 8));
        this.registerBlueprint("steamfurnacemk2", Multiblocks.STEAM_FURNACE_MK2);
        this.registerBlueprint("solarmirror", Multiblocks.SOLAR_MIRROR);

        this.getBlueprint("steamfurnacemk1").setMultiblockSteps(this.loadBlueprintModel("steamfurnacemk1"));
    }

    public Blueprint registerBlueprint(final String name, final IMultiblockDescriptor multiblock)
    {
        this.blueprints.put(name, new Blueprint(name, multiblock, multiblock.getBlockCount() * 4));
        return this.blueprints.get(name);
    }

    private ArrayList<MultiblockStep> loadBlueprintModel(final String name)
    {
        MultiblockStep[] steps = null;
        try
        {
            final InputStream stream = Blueprints.class
                    .getResourceAsStream("/assets/" + QBar.MODID + "/multiblock/" + name + ".hjson");
            steps = this.gson.fromJson(JsonValue.readHjson(IOUtils.toString(stream, StandardCharsets.UTF_8)).toString(),
                    MultiblockStep[].class);
            stream.close();
            return Lists.newArrayList(steps);
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
        return null;
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
