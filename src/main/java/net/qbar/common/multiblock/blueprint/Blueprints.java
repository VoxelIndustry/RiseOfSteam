package net.qbar.common.multiblock.blueprint;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.IMultiblockDescriptor;
import net.qbar.common.multiblock.Multiblocks;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hjson.JsonValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

        this.registerBlueprint("fluidtank_medium", Multiblocks.MEDIUM_FLUID_TANK)
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Items.IRON_DOOR, 4));
        this.registerBlueprint("fluidtank_big", Multiblocks.BIG_FLUID_TANK)
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Items.IRON_DOOR, 4));

        this.registerBlueprint("solid_boiler", Multiblocks.SOLID_BOILER)
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Items.IRON_DOOR, 4));
        this.registerBlueprint("assembler", Multiblocks.ASSEMBLER).addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4))
                .addStep(10, new ItemStack(Items.IRON_DOOR, 4));
        this.registerBlueprint("solar_boiler", Multiblocks.SOLAR_BOILER)
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Items.IRON_DOOR, 4))
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4));
        this.registerBlueprint("keypunch", Multiblocks.KEYPUNCH).addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4))
                .addStep(10, new ItemStack(Items.IRON_DOOR, 4));
        this.registerBlueprint("liquidfuel_boiler", Multiblocks.LIQUID_FUEL_BOILER)
                .addStep(20, new ItemStack(Blocks.IRON_BLOCK, 9)).addStep(20, new ItemStack(Blocks.IRON_BLOCK, 4))
                .addStep(10, new ItemStack(Items.CARROT, 16));
        this.registerBlueprint("rollingmill", Multiblocks.ROLLING_MILL).addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4))
                .addStep(10, new ItemStack(Items.IRON_DOOR, 4)).addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4));
        this.registerBlueprint("steamfurnacemk1", Multiblocks.STEAM_FURNACE_MK1)
                .addStep(20, new ItemStack(Blocks.BRICK_BLOCK, 4)).addStep(10, new ItemStack(QBarItems.IRON_ROD, 8));
        this.registerBlueprint("steamfurnacemk2", Multiblocks.STEAM_FURNACE_MK2)
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4)).addStep(10, new ItemStack(Items.IRON_DOOR, 4))
                .addStep(10, new ItemStack(Blocks.IRON_BLOCK, 4));
        this.registerBlueprint("solarmirror", Multiblocks.SOLAR_MIRROR).addStep(10,
                new ItemStack(Blocks.IRON_BLOCK, 4));

        for (Map.Entry<String, Blueprint> blueprint : this.blueprints.entrySet())
            blueprint.getValue().setMultiblockSteps(this.loadBlueprintModel(blueprint.getKey()));
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
            ArrayList<MultiblockStep> rtn = Lists.newArrayList(steps);

            for (MultiblockStep step : rtn)
            {
                if (rtn.indexOf(step) != 0)
                    step.setParts(ArrayUtils.addAll(step.getParts(), rtn.get(rtn.indexOf(step) - 1).getParts()));
            }

            return rtn;
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<>();
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
