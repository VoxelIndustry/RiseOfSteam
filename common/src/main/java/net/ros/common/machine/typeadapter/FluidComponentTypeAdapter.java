package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ros.common.machine.component.FluidComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;

public class FluidComponentTypeAdapter extends TypeAdapter<FluidComponent>
        implements IMachineComponentTypeAdapter<FluidComponent>
{
    @Override
    public void write(JsonWriter out, FluidComponent value)
    {

    }

    @Override
    public Class<FluidComponent> getComponentClass()
    {
        return FluidComponent.class;
    }

    @Override
    public FluidComponent read(JsonReader in) throws IOException
    {
        FluidComponent component = new FluidComponent();

        in.beginArray();
        while (in.hasNext())
        {
            String tankName = "";
            int capacity = 0;
            int throttle = Integer.MAX_VALUE;

            in.beginObject();
            while (in.hasNext())
            {
                switch (in.nextName())
                {
                    case "name":
                        tankName = in.nextString();
                        break;
                    case "capacity":
                        capacity = in.nextInt();
                        break;
                    case "throttle":
                        throttle = in.nextInt();
                        break;
                    default:
                        break;
                }
            }
            in.endObject();

            component.getTanks().put(tankName, Pair.of(capacity, throttle));
        }
        in.endArray();

        return component;
    }
}
