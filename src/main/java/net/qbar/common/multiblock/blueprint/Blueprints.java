package net.qbar.common.multiblock.blueprint;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.Getter;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.qbar.QBar;
import net.qbar.common.init.QBarBlocks;
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

    @Getter
    private final HashMap<String, Blueprint> blueprints;
    private final Gson                       gson;

    private Blueprints()
    {
        this.gson = new Gson();
        this.blueprints = new HashMap<>();

        this.registerBlueprint("fluidtank_small", Multiblocks.SMALL_FLUID_TANK)
                .addStep(20, new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("iron")))
                .addStep(15, new ItemStack(QBarBlocks.FLUID_PIPE, 4), new ItemStack(Blocks.GLASS_PANE));

        this.registerBlueprint("fluidtank_medium", Multiblocks.MEDIUM_FLUID_TANK)
                .addStep(30, new ItemStack(QBarItems.METALPLATE, 24, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(20, new ItemStack(QBarBlocks.FLUID_PIPE, 8), new ItemStack(Blocks.GLASS_PANE));

        this.registerBlueprint("fluidtank_big", Multiblocks.BIG_FLUID_TANK)
                .addStep(40, new ItemStack(QBarItems.METALPLATE, 64, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(30, new ItemStack(QBarBlocks.FLUID_PIPE, 16), new ItemStack(Blocks.GLASS_PANE));

        this.registerBlueprint("solid_boiler", Multiblocks.SOLID_BOILER)
                .addStep(20, new ItemStack(QBarItems.METALPLATE, 10, QBarItems.METALPLATE.getMetalMeta("brass")))
                .addStep(15, new ItemStack(QBarBlocks.STEAM_PIPE, 4), new ItemStack(QBarBlocks.FLUID_PIPE, 2));

        this.registerBlueprint("assembler", Multiblocks.ASSEMBLER)
                .addStep(15, new ItemStack(QBarItems.METALPLATE, 16, QBarItems.METALPLATE.getMetalMeta("iron")),
                        new ItemStack(QBarItems.GEARBOX), new ItemStack(QBarItems.LOGICBOX))
                .addStep(10, new ItemStack(QBarBlocks.STEAM_PIPE), new ItemStack(QBarBlocks.BELT));

        this.registerBlueprint("solar_boiler", Multiblocks.SOLAR_BOILER)
                .addStep(20, new ItemStack(Items.IRON_INGOT, 10))
                .addStep(45, new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("brass")),
                        new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(20, new ItemStack(QBarBlocks.STEAM_PIPE, 40), new ItemStack(Blocks.GLASS_PANE));

        this.registerBlueprint("liquidfuel_boiler", Multiblocks.LIQUID_FUEL_BOILER)
                .addStep(30, new ItemStack(QBarItems.METALPLATE, 30, QBarItems.METALPLATE.getMetalMeta("brass")),
                        new ItemStack(QBarItems.METALPLATE, 10, QBarItems.METALPLATE.getMetalMeta("iron")))
                .addStep(15, new ItemStack(QBarBlocks.STEAM_PIPE, 4), new ItemStack(QBarBlocks.FLUID_PIPE, 4));

        this.registerBlueprint("rollingmill", Multiblocks.ROLLING_MILL)
                .addStep(30, new ItemStack(QBarItems.METALPLATE, 15, QBarItems.METALPLATE.getMetalMeta("brass")),
                        new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("iron")))
                .addStep(20, new ItemStack(QBarItems.GEARBOX, 2), new ItemStack(QBarBlocks.BELT));

        this.registerBlueprint("steamfurnacemk1", Multiblocks.STEAM_FURNACE_MK1)
                .addStep(20, new ItemStack(Blocks.BRICK_BLOCK, 9))
                .addStep(15, new ItemStack(QBarBlocks.STEAM_PIPE, 2), new ItemStack(QBarBlocks.BELT, 2));

        this.registerBlueprint("steamfurnacemk2", Multiblocks.STEAM_FURNACE_MK2)
                .addStep(20, new ItemStack(Blocks.BRICK_BLOCK, 20))
                .addStep(30, new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("bronze")),
                        new ItemStack(QBarItems.METALPLATE, 10, QBarItems.METALPLATE.getMetalMeta("steel")))
                .addStep(15, new ItemStack(QBarBlocks.STEAM_PIPE, 4), new ItemStack(QBarBlocks.BELT, 3));

        this.registerBlueprint("solar_mirror", Multiblocks.SOLAR_MIRROR)
                .addStep(10, new ItemStack(Items.IRON_INGOT, 6));

        this.registerBlueprint("ore_washer", Multiblocks.ORE_WASHER)
                .addStep(15, new ItemStack(Items.IRON_INGOT, 20))
                .addStep(20, new ItemStack(QBarItems.METALPLATE, 20, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(15, new ItemStack(QBarBlocks.STEAM_PIPE, 4), new ItemStack(QBarBlocks.FLUID_PIPE, 2),
                        new ItemStack(QBarBlocks.BELT, 3));

        this.registerBlueprint("sorting_machine", Multiblocks.SORTING_MACHINE)
                .addStep(15, new ItemStack(Items.IRON_INGOT, 15))
                .addStep(20, new ItemStack(QBarItems.METALPLATE, 10, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(10, new ItemStack(QBarBlocks.STEAM_PIPE, 3));

        this.registerBlueprint("small_mining_drill", Multiblocks.SMALL_MINING_DRILL)
                .addStep(25, new ItemStack(Items.IRON_INGOT, 48))
                .addStep(40, new ItemStack(QBarItems.METALPLATE, 32, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(30, new ItemStack(QBarItems.GEARBOX, 4), new ItemStack(QBarBlocks.STEAM_PIPE, 10),
                        new ItemStack(QBarBlocks.FLUID_PIPE, 10), new ItemStack(QBarBlocks.BELT));

        this.registerBlueprint("tiny_mining_drill", Multiblocks.TINY_MINING_DRILL)
                .addStep(15, new ItemStack(Blocks.PLANKS, 4), new ItemStack(Items.IRON_INGOT, 12))
                .addStep(10, new ItemStack(QBarItems.GEARBOX));

        this.registerBlueprint("alloy_cauldron", Multiblocks.ALLOY_CAULDRON)
                .addStep(40, new ItemStack(QBarItems.METALPLATE, 64, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(30, new ItemStack(QBarBlocks.FLUID_PIPE, 16), new ItemStack(Blocks.GLASS_PANE));

        this.registerBlueprint("saw_mill", Multiblocks.SAW_MILL)
                .addStep(40, new ItemStack(QBarItems.METALPLATE, 64, QBarItems.METALPLATE.getMetalMeta("bronze")))
                .addStep(30, new ItemStack(QBarBlocks.FLUID_PIPE, 16), new ItemStack(Blocks.GLASS_PANE));

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

    public Blueprint getBlueprint(final String name)
    {
        return this.blueprints.get(name);
    }
}
