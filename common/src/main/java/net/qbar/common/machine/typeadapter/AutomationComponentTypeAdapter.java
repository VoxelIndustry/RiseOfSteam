package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.machine.component.AutomationComponent;
import net.qbar.common.machine.OutputPoint;
import net.qbar.common.multiblock.MultiblockSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        BlockPos pos = BlockPos.ORIGIN;
        EnumFacing facing = EnumFacing.NORTH;

        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "pos":
                    in.beginArray();
                    pos = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                case "facing":
                    facing = EnumFacing.byName(in.nextString());
                    break;
                case "slots":
                    List<Integer> slots = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext())
                        slots.add(in.nextInt());
                    in.endArray();

                    outputPoint.setSlots(slots.toArray(new Integer[slots.size()]));
                    break;
                case "order":
                    if ("balanced".equals(in.nextString()))
                        outputPoint.setRoundRobin(true);
                    break;
                default:
                    break;
            }
        }
        outputPoint.setSide(new MultiblockSide(pos, facing));
        component.getOutputs().add(outputPoint);
    }
}