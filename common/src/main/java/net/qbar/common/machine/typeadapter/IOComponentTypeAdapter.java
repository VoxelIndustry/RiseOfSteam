package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.machine.FluidIOPoint;
import net.qbar.common.machine.component.IOComponent;
import net.qbar.common.multiblock.MultiblockSide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        BlockPos pos = BlockPos.ORIGIN;
        EnumFacing facing = EnumFacing.NORTH;

        BlockPos firstPoint = BlockPos.ORIGIN;
        BlockPos secondPoint = BlockPos.ORIGIN;

        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "pos":
                    in.beginArray();
                    pos = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                case "fromPos":
                    in.beginArray();
                    firstPoint = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                case "toPos":
                    in.beginArray();
                    secondPoint = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                case "facing":
                    facing = EnumFacing.byName(in.nextString());
                    break;
                default:
                    break;
            }
        }

        if (firstPoint != BlockPos.ORIGIN || secondPoint != BlockPos.ORIGIN)
        {
            EnumFacing finalFacing = facing;

            return StreamSupport.stream(BlockPos.getAllInBox(firstPoint, secondPoint).spliterator(), false)
                    .map(blockPos -> new MultiblockSide(blockPos, finalFacing)).collect(Collectors.toList());
        }
        return Collections.singletonList(new MultiblockSide(pos, facing));
    }

    private List<FluidIOPoint> parseFluid(JsonReader in, String tank) throws IOException
    {
        FluidIOPoint point = new FluidIOPoint();
        BlockPos pos = BlockPos.ORIGIN;
        EnumFacing facing = EnumFacing.NORTH;

        BlockPos firstPoint = BlockPos.ORIGIN;
        BlockPos secondPoint = BlockPos.ORIGIN;

        point.setInput(true);
        point.setOutput(true);
        point.setTankName(tank);
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
                        point.setOutput(false);
                    if ("output-only".equals(restriction))
                        point.setInput(false);
                    break;
                case "tank":
                    point.setTankName(in.nextString());
                    break;
                case "fromPos":
                    in.beginArray();
                    firstPoint = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                case "toPos":
                    in.beginArray();
                    secondPoint = new BlockPos(in.nextInt(), in.nextInt(), in.nextInt());
                    in.endArray();
                    break;
                default:
                    break;
            }
        }
        point.setSide(new MultiblockSide(pos, facing));

        if (firstPoint != BlockPos.ORIGIN || secondPoint != BlockPos.ORIGIN)
        {
            EnumFacing finalFacing = facing;

            return StreamSupport.stream(BlockPos.getAllInBox(firstPoint, secondPoint).spliterator(), false)
                    .map(blockPos ->
                    {
                        FluidIOPoint fluidPoint = new FluidIOPoint();
                        fluidPoint.setTankName(point.getTankName());
                        fluidPoint.setInput(point.isInput());
                        fluidPoint.setOutput(point.isOutput());
                        fluidPoint.setSide(new MultiblockSide(blockPos, finalFacing));
                        return fluidPoint;
                    }).collect(Collectors.toList());
        }
        return Collections.singletonList(point);
    }
}