package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.ros.common.machine.InputPoint;
import net.ros.common.machine.OutputPoint;
import net.ros.common.machine.component.AutomationComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AutomationComponentTypeAdapter extends TypeAdapter<AutomationComponent>
        implements IMachineComponentTypeAdapter<AutomationComponent>
{
    @Override
    public void write(JsonWriter out, AutomationComponent value) throws IOException
    {

    }

    @Override
    public Class<AutomationComponent> getComponentClass()
    {
        return AutomationComponent.class;
    }

    @Override
    public AutomationComponent read(JsonReader in) throws IOException
    {
        AutomationComponent component = new AutomationComponent();

        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "outputs":
                    in.beginArray();
                    while (in.hasNext())
                    {
                        in.beginObject();
                        parseOutputPoint(in, component);
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "inputs":
                    in.beginArray();
                    while (in.hasNext())
                    {
                        in.beginObject();
                        parseInputPoint(in, component);
                        in.endObject();
                    }
                    in.endArray();
                default:
                    break;
            }
        }
        in.endObject();

        return component;
    }

    private void parseOutputPoint(JsonReader in, AutomationComponent component) throws IOException
    {
        OutputPoint outputPoint = new OutputPoint();
        outputPoint.setInventory("undefined");

        MultiblockSideParser sideParser = new MultiblockSideParser();

        while (in.hasNext())
        {
            String key = in.nextName();
            switch (key)
            {
                case "slots":
                    List<Integer> slots = new ArrayList<>();

                    if (in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        in.beginArray();
                        while (in.hasNext())
                            slots.add(in.nextInt());
                        in.endArray();
                    }
                    else
                    {
                        String value = in.nextString();

                        slots = IntStream.rangeClosed(Integer.valueOf(value.split("\\.\\.")[0]),
                                Integer.valueOf(value.split("\\.\\.")[1])).boxed().collect(Collectors.toList());
                    }
                    outputPoint.setSlots(slots.stream().mapToInt(Integer::intValue).toArray());
                    break;
                case "order":
                    if ("balanced".equals(in.nextString()))
                        outputPoint.setRoundRobin(true);
                    break;
                case "inventory":
                    outputPoint.setInventory(in.nextString());
                    break;
                case "connText":
                    outputPoint.setConnText(in.nextString());
                    break;
                default:
                    if (sideParser.isKey(key))
                        sideParser.parse(key, in);
                    break;
            }
        }

        component.getOutputs().addAll(sideParser.get().stream().map(side ->
        {
            OutputPoint copy = new OutputPoint();
            copy.setInventory(outputPoint.getInventory());
            copy.setSlots(outputPoint.getSlots());
            copy.setRoundRobin(outputPoint.isRoundRobin());
            copy.setConnText(outputPoint.getConnText());
            copy.setSide(side);

            return copy;
        }).collect(Collectors.toList()));
    }

    private void parseInputPoint(JsonReader in, AutomationComponent component) throws IOException
    {
        InputPoint inputPoint = new InputPoint();
        inputPoint.setInventory("undefined");

        MultiblockSideParser sideParser = new MultiblockSideParser();

        while (in.hasNext())
        {
            String key = in.nextName();
            switch (key)
            {
                case "slots":
                    List<Integer> slots = new ArrayList<>();

                    if (in.peek() == JsonToken.BEGIN_ARRAY)
                    {
                        in.beginArray();
                        while (in.hasNext())
                            slots.add(in.nextInt());
                        in.endArray();
                    }
                    else
                    {
                        String value = in.nextString();

                        slots = IntStream.rangeClosed(Integer.valueOf(value.split("\\.\\.")[0]),
                                Integer.valueOf(value.split("\\.\\.")[1])).boxed().collect(Collectors.toList());
                    }
                    inputPoint.setSlots(slots.stream().mapToInt(Integer::intValue).toArray());
                    break;
                case "inventory":
                    inputPoint.setInventory(in.nextString());
                    break;
                case "connText":
                    inputPoint.setConnText(in.nextString());
                    break;
                default:
                    if (sideParser.isKey(key))
                        sideParser.parse(key, in);
                    break;
            }
        }

        component.getInputs().addAll(sideParser.get().stream().map(side ->
        {
            InputPoint copy = new InputPoint();
            copy.setInventory(inputPoint.getInventory());
            copy.setSlots(inputPoint.getSlots());
            copy.setConnText(inputPoint.getConnText());
            copy.setSide(side);

            return copy;
        }).collect(Collectors.toList()));
    }
}