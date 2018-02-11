package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.qbar.common.machine.EMachineTier;
import net.qbar.common.machine.EMachineType;
import net.qbar.common.machine.MachineDescriptor;
import net.qbar.common.machine.QBarMachines;

import java.io.IOException;
import java.util.HashMap;

public class MachineDescriptorTypeAdapter extends TypeAdapter<MachineDescriptor>
{
    private HashMap<String, IMachineComponentTypeAdapter> subTypeAdapters;

    public MachineDescriptorTypeAdapter()
    {
        subTypeAdapters = new HashMap<>();

        subTypeAdapters.put("steam", new SteamComponentTypeAdapter());
        subTypeAdapters.put("multiblock", new MultiblockComponentTypeAdapter());
        subTypeAdapters.put("blueprint", new BlueprintComponentTypeAdapter());
        subTypeAdapters.put("crafter", new CraftingComponentTypeAdapter());
        subTypeAdapters.put("automation", new AutomationComponentTypeAdapter());
    }

    @Override
    public void write(JsonWriter out, MachineDescriptor value)
    {

    }

    @Override
    public MachineDescriptor read(JsonReader in) throws IOException
    {
        MachineDescriptor descriptor = new MachineDescriptor();

        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "tier":
                    descriptor.setTier(EMachineTier.values()[in.nextInt()]);
                    break;
                case "type":
                    descriptor.setType(EMachineType.valueOf(in.nextString()));
                    break;
                case "components":
                    descriptor.setComponents(new HashMap<>());
                    in.beginObject();
                    while (in.hasNext())
                    {
                        if (QBarMachines.isPreloading())
                        {
                            descriptor.getComponents().put(
                                    this.subTypeAdapters.get(in.nextName()).getComponentClass(), null);
                            in.skipValue();
                        }
                        else
                            descriptor.component(this.subTypeAdapters.get(in.nextName()).read(in));
                    }
                    in.endObject();
                    break;
                default:
                    break;
            }
        }
        in.endObject();
        return descriptor;
    }
}
