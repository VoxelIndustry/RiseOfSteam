package net.qbar.common.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.qbar.QBar;
import net.qbar.common.machine.typeadapter.MachineDescriptorTypeAdapter;
import org.apache.commons.io.IOUtils;
import org.hjson.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class QBarMachines
{
    public static MachineDescriptor                                                    OFFSHORE_PUMP;
    public static MachineDescriptor                                                    SMALL_MINING_DRILL;

    public static MachineDescriptor                                                    ROLLING_MILL;
    public static MachineDescriptor                                                    FURNACE_MK1;
    public static MachineDescriptor                                                    FURNACE_MK2;
    public static MachineDescriptor                                                    ASSEMBLER;

    public static MachineDescriptor                                                    ORE_WASHER;
    public static MachineDescriptor                                                    SORTING_MACHINE;
    public static MachineDescriptor                                                    SAW_MILL;

    private static Set<MachineDescriptor>                                              machines = new HashSet<>();
    private static HashMap<Class<? extends IMachineComponent>, Set<MachineDescriptor>> subLists = new HashMap<>();

    private static final Gson                                                          GSON     = new GsonBuilder()
            .registerTypeAdapter(MachineDescriptor.class, new MachineDescriptorTypeAdapter()).create();

    static
    {
        /*
         * OFFSHORE_PUMP = new MachineDescriptor("offshorepump", EMachineTier.TIER1,
         * EMachineType.LOGISTIC)
         * .component(SteamComponent.builder().steamCapacity(2000).steamConsumption(5)
         * .workingPressure(SteamUtil.BASE_PRESSURE).maxPressureCapacity(1.5f *
         * SteamUtil.BASE_PRESSURE) .allowOvercharge(true).build());
         * 
         * SMALL_MINING_DRILL = new MachineDescriptor("smallminingdrill",
         * EMachineTier.TIER1, EMachineType.RESOURCE_PRODUCER)
         * .component(SteamComponent.builder().steamCapacity(2000).steamConsumption(25)
         * .workingPressure(SteamUtil.BASE_PRESSURE).maxPressureCapacity(2 *
         * SteamUtil.BASE_PRESSURE) .allowOvercharge(true).build()) .component(new
         * MultiblockComponent(3,3,3,1,0,1)) .component(new Blueprint());
         * 
         * SMALL_MINING_DRILL = MachineDescriptor.builder().name("smallminingdrill")
         * .steamCapacity(2000).steamConsumption(25)
         * .workingPressure(SteamUtil.BASE_PRESSURE).maxPressureCapacity(2 *
         * SteamUtil.BASE_PRESSURE) .allowOvercharge(true).build();
         * 
         * ROLLING_MILL = new CraftingComponent.Builder("rollingmill")
         * .recipe(QBarRecipeHandler.ROLLINGMILL_UID, 1f).inventory(3, 1, 1)
         * .steam(2000, 10, SteamUtil.BASE_PRESSURE, 1.5f * SteamUtil.BASE_PRESSURE,
         * true).create();
         * 
         * FURNACE_MK1 = new
         * CraftingComponent.Builder("furnacemk1").recipe(QBarRecipeHandler.FURNACE_UID,
         * 1f) .inventory(3, 1, 1).steam(2000, 10, SteamUtil.BASE_PRESSURE, 1.5f *
         * SteamUtil.BASE_PRESSURE, true) .create();
         * 
         * FURNACE_MK2 = new
         * CraftingComponent.Builder("furnacemk2").recipe(QBarRecipeHandler.FURNACE_UID,
         * 1f) .inventory(3, 1, 1).steam(2000, 10, SteamUtil.BASE_PRESSURE, 1.5f *
         * SteamUtil.BASE_PRESSURE, true) .create();
         * 
         * ASSEMBLER = new CraftingComponent.Builder("assembler").recipe("",
         * 1f).inventory(3, 1, 1) .steam(2000, 10, SteamUtil.BASE_PRESSURE, 1.5f *
         * SteamUtil.BASE_PRESSURE, true).create();
         * 
         * ORE_WASHER = new
         * CraftingComponent.Builder("orewasher").recipe(QBarRecipeHandler.
         * ORE_WASHER_UID, 1f) .inventory(4, 1, 2).inputTanks(new
         * int[]{Fluid.BUCKET_VOLUME * 8}) .steam(2000, 10, SteamUtil.BASE_PRESSURE,
         * 1.5f * SteamUtil.BASE_PRESSURE, true).create();
         * 
         * SORTING_MACHINE = new CraftingComponent.Builder("sortingmachine")
         * .recipe(QBarRecipeHandler.SORTING_MACHINE_UID, 1f).inventory(6, 1, 4)
         * .steam(2000, 10, SteamUtil.BASE_PRESSURE, 1.5f * SteamUtil.BASE_PRESSURE,
         * true).create();
         * 
         * SAW_MILL = new CraftingComponent.Builder("sawmill")
         * .recipe(QBarRecipeHandler.SAW_MILL_UID, 1f).inventory(3, 1, 1) .steam(2000,
         * 10, SteamUtil.BASE_PRESSURE, 1.5f * SteamUtil.BASE_PRESSURE, true) .create();
         */
    }

    public static void loadMachines()
    {
        SMALL_MINING_DRILL = loadMachine("smallminingdrill");
    }

    public static Set<MachineDescriptor> getAllByComponent(Class<? extends IMachineComponent> componentType)
    {
        if (!subLists.containsKey(componentType))
            subLists.put(componentType,
                    machines.stream().filter(descriptor -> descriptor.has(componentType)).collect(Collectors.toSet()));
        return subLists.get(componentType);
    }

    public static Set<MachineDescriptor> getAll()
    {
        return machines;
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

    private static MachineDescriptor loadMachine(String name)
    {
        MachineDescriptor descriptor = null;
        final InputStream stream = QBarMachines.class
                .getResourceAsStream("/assets/" + QBar.MODID + "/machines/" + name + ".hjson");
        try
        {
            descriptor = GSON.fromJson(JsonValue.readHjson(IOUtils.toString(stream, StandardCharsets.UTF_8)).toString(),
                    MachineDescriptor.class);
            stream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        descriptor.setName(name);
        machines.add(descriptor);
        return descriptor;
    }
}