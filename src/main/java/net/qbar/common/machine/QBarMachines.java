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

    public static MachineDescriptor                                                    SMALL_FLUID_TANK;
    public static MachineDescriptor                                                    MEDIUM_FLUID_TANK;
    public static MachineDescriptor                                                    BIG_FLUID_TANK;

    public static MachineDescriptor                                                    SOLID_BOILER;
    public static MachineDescriptor                                                    SOLAR_BOILER;
    public static MachineDescriptor                                                    LIQUID_BOILER;

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