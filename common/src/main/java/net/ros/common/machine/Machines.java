package net.ros.common.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ros.common.ROSConstants;
import net.ros.common.machine.typeadapter.MachineDescriptorTypeAdapter;
import net.ros.common.multiblock.blueprint.Blueprint;
import org.apache.commons.io.IOUtils;
import org.hjson.JsonValue;
import org.hjson.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Machines
{
    public static MachineDescriptor OFFSHORE_PUMP;
    public static MachineDescriptor SMALL_FLUID_TANK;
    public static MachineDescriptor MEDIUM_FLUID_TANK;
    public static MachineDescriptor BIG_FLUID_TANK;
    public static MachineDescriptor SOLID_BOILER;
    public static MachineDescriptor SOLAR_BOILER;
    public static MachineDescriptor LIQUID_BOILER;
    public static MachineDescriptor SMALL_MINING_DRILL;
    public static MachineDescriptor ROLLING_MILL;
    public static MachineDescriptor FURNACE_MK1;
    public static MachineDescriptor FURNACE_MK2;
    public static MachineDescriptor ASSEMBLER;
    public static MachineDescriptor ORE_WASHER;
    public static MachineDescriptor SORTING_MACHINE;
    public static MachineDescriptor SAW_MILL;
    public static MachineDescriptor ALLOY_CAULDRON;
    public static MachineDescriptor BLUEPRINT_PRINTER;
    public static MachineDescriptor CRAFTCARD_LIBRARY;
    public static MachineDescriptor ENGINEER_WORKBENCH;
    public static MachineDescriptor ENGINEER_STORAGE;
    public static MachineDescriptor KEYPUNCH;
    public static MachineDescriptor SOLAR_MIRROR;
    public static MachineDescriptor TINY_MINING_DRILL;
    public static MachineDescriptor CAPSULE_FILLER;
    public static MachineDescriptor SMALL_STEAM_TANK;
    public static MachineDescriptor MEDIUM_STEAM_TANK;
    public static MachineDescriptor ITEM_SPLITTER;
    public static MachineDescriptor ITEM_EXTRACTOR;
    public static MachineDescriptor STEAM_VENT;

    private static HashMap<String, MachineDescriptor>                                  machines = new HashMap<>();
    private static HashMap<Class<? extends IMachineComponent>, Set<MachineDescriptor>> subLists = new HashMap<>();

    public static final Gson    GSON = new GsonBuilder()
            .registerTypeAdapter(MachineDescriptor.class, new MachineDescriptorTypeAdapter()).create();
    private static      boolean preload;

    public static void preLoadMachines()
    {
        preload = true;
        doLoad();
    }

    public static void loadMachines()
    {
        preload = false;
        doLoad();
    }

    private static void doLoad()
    {
        if (!isPreloading())
        {
            machines.clear();
            subLists.clear();
        }

        SMALL_MINING_DRILL = loadMachine("smallminingdrill");
        OFFSHORE_PUMP = loadMachine("offshore_pump");
        SMALL_FLUID_TANK = loadMachine("fluidtank_small");
        MEDIUM_FLUID_TANK = loadMachine("fluidtank_medium");
        BIG_FLUID_TANK = loadMachine("fluidtank_big");
        ALLOY_CAULDRON = loadMachine("alloycauldron");
        ASSEMBLER = loadMachine("assembler");
        BLUEPRINT_PRINTER = loadMachine("blueprintprinter");
        CRAFTCARD_LIBRARY = loadMachine("craftcardlibrary");
        ENGINEER_WORKBENCH = loadMachine("engineer_workbench");
        ENGINEER_STORAGE = loadMachine("engineer_storage");
        KEYPUNCH = loadMachine("keypunch");
        LIQUID_BOILER = loadMachine("liquidfuel_boiler");
        ORE_WASHER = loadMachine("orewasher");
        ROLLING_MILL = loadMachine("rollingmill");
        SAW_MILL = loadMachine("sawmill");
        SOLAR_BOILER = loadMachine("solar_boiler");
        SOLAR_MIRROR = loadMachine("solar_mirror");
        SOLID_BOILER = loadMachine("solid_boiler");
        SORTING_MACHINE = loadMachine("sortingmachine");
        FURNACE_MK1 = loadMachine("steamfurnacemk1");
        FURNACE_MK2 = loadMachine("steamfurnacemk2");
        TINY_MINING_DRILL = loadMachine("tinyminingdrill");
        CAPSULE_FILLER = loadMachine("capsulefiller");
        SMALL_STEAM_TANK = loadMachine("steamtank_small");
        MEDIUM_STEAM_TANK = loadMachine("steamtank_medium");
        ITEM_SPLITTER = loadMachine("item_splitter");
        ITEM_EXTRACTOR = loadMachine("item_extractor");
        STEAM_VENT = loadMachine("steam_vent");

        if (!isPreloading())
            getAllByComponent(Blueprint.class).forEach(new BlueprintLoader());
    }

    public static Set<MachineDescriptor> getAllByComponent(Class<? extends IMachineComponent> componentType)
    {
        if (!subLists.containsKey(componentType))
            subLists.put(componentType,
                    machines.values().stream().filter(descriptor -> descriptor.has(componentType)).collect(Collectors
                            .toSet()));
        return subLists.get(componentType);
    }

    public static Collection<MachineDescriptor> getAll()
    {
        return machines.values();
    }

    public static boolean contains(Class<? extends IMachineComponent> componentType, String name)
    {
        return getAllByComponent(componentType).stream().anyMatch(descriptor -> descriptor.getName().equals(name));
    }

    public static <T extends IMachineComponent> T getComponent(Class<T> componentType, String name)
    {
        Optional<MachineDescriptor> desc = getAllByComponent(componentType).stream()
                .filter(descriptor -> descriptor.getName().equals(name)).findAny();

        if (desc.isPresent())
            return desc.get().get(componentType);
        return null;
    }

    public static MachineDescriptor get(String name)
    {
        return machines.get(name);
    }

    public static boolean isPreloading()
    {
        return preload;
    }

    private static MachineDescriptor loadMachine(String name)
    {
        MachineDescriptor descriptor = null;
        final InputStream stream = Machines.class
                .getResourceAsStream("/assets/" + ROSConstants.MODID + "/machines/" + name + ".hjson");
        try
        {
            descriptor = GSON.fromJson(JsonValue.readHjson(IOUtils.toString(stream, StandardCharsets.UTF_8)).toString(),
                    MachineDescriptor.class);
            stream.close();
            descriptor.setName(name);
            machines.put(name, descriptor);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            ROSConstants.LOGGER.error("Exception thrown while parsing " + name, e);
        }
        return descriptor;
    }
}
