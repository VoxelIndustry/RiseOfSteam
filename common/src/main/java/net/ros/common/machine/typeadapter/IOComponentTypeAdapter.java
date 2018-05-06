package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.ros.common.machine.FluidIOPoint;
import net.ros.common.machine.component.IOComponent;
import net.ros.common.multiblock.MultiblockSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IOComponentTypeAdapter extends TypeAdapter<IOComponent>
        implements IMachineComponentTypeAdapter<IOComponent>
{
    @Override
    public void write(JsonWriter out, IOComponent value)
    {

    }

    @Override
    public Class<IOComponent> getComponentClass()
    {
        return IOComponent.class;
    }

    @Override
    public IOComponent read(JsonReader in) throws IOException
    {
        IOComponent component = new IOComponent();

        List<MultiblockSide> steamIO = new ArrayList<>();
        List<FluidIOPoint> fluidIO = new ArrayList<>();
        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "steam":
                    in.beginArray();
                    while (in.hasNext())
                    {
                        in.beginObject();
                        steamIO.addAll(parseSide(in));
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "fluid":
                    if (in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        in.beginArray();
                        while (in.hasNext())
                        {
                            in.beginObject();
                            fluidIO.addAll(parseFluid(in, ""));
                            in.endObject();
                        }
                        in.endArray();
                    }
                    else if (in.peek() == JsonToken.BEGIN_OBJECT)
                    {
                        in.beginObject();
                        while (in.hasNext())
                        {
                            String tank = in.nextName();

                            in.beginArray();
                            while (in.hasNext())
                            {
                                in.beginObject();
                                fluidIO.addAll(parseFluid(in, tank));
                                in.endObject();
                            }
                            in.endArray();
                        }
                        in.endObject();
                    }
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        component.setSteamIO(steamIO.toArray(new MultiblockSide[0]));
        component.setFluidIO(fluidIO.toArray(new FluidIOPoint[0]));
        return component;
    }

    private List<MultiblockSide> parseSide(JsonReader in) throws IOException
    {
        MultiblockSideParser sideParser = new MultiblockSideParser();

        while (in.hasNext())
        {
            String key = in.nextName();

            if (sideParser.isKey(key))
                sideParser.parse(key, in);
        }

        return sideParser.get();
    }

    private List<FluidIOPoint> parseFluid(JsonReader in, String tank) throws IOException
    {
        FluidIOPoint point = new FluidIOPoint();
        point.setInput(true);
        point.setOutput(true);
        point.setTankName(tank);

        MultiblockSideParser sideParser = new MultiblockSideParser();

        while (in.hasNext())
        {
            String key = in.nextName();
            switch (key)
            {
                case "restriction":
                    String restriction = in.nextString();
                    if ("input-only".equals(restriction))
                        point.setOutput(false);
                    if ("output-only".equals(restriction))
                        point.setInput(false);
                    break;
                case "tank":
                    point.setTankName(in.nextString());
                    break;
                default:
                    if (sideParser.isKey(key))
                        sideParser.parse(key, in);
                    break;
            }
        }

        return sideParser.get().stream().map(side ->
        {
            FluidIOPoint copy = new FluidIOPoint();
            copy.setTankName(point.getTankName());
            copy.setInput(point.isInput());
            copy.setOutput(point.isOutput());
            copy.setSide(side);

            return copy;
        }).collect(Collectors.toList());
    }
}