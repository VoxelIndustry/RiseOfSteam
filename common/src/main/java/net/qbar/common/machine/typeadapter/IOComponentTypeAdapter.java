package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.machine.FluidIOPoint;
import net.qbar.common.machine.component.IOComponent;
import net.qbar.common.multiblock.MultiblockSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                        steamIO.add(parseSteam(in));
                        in.endObject();
                    }
                    in.endArray();
                    break;
                case "fluid":
                    in.beginArray();
                    while (in.hasNext())
                    {
                        in.beginObject();
                        fluidIO.add(parseFluid(in));
                        in.endObject();
                    }
                    in.endArray();
                default:
                    break;
            }
        }
        in.endObject();

        component.setSteamIO(steamIO.toArray(new MultiblockSide[steamIO.size()]));
        component.setFluidIO(fluidIO.toArray(new FluidIOPoint[fluidIO.size()]));
        return component;
    }

    private MultiblockSide parseSteam(JsonReader in) throws IOException
    {
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
                default:
                    break;
            }
        }
        return new MultiblockSide(pos, facing);
    }

    private FluidIOPoint parseFluid(JsonReader in) throws IOException
    {
        FluidIOPoint point = new FluidIOPoint();
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
                case "restriction":
                    String restriction = in.nextString();
                    if ("input-only".equals(restriction))
                        point.setInput(true);
                    if ("output-only".equals(restriction))
                        point.setOutput(true);
                    break;
                case "tank":
                    point.setTankName(in.nextString());
                    break;
                default:
                    break;
            }
        }
        point.setSide(new MultiblockSide(pos, facing));
        return point;
    }
}