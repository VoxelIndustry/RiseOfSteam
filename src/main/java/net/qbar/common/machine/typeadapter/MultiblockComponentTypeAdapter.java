package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.qbar.common.multiblock.MultiblockComponent;

import java.io.IOException;

public class MultiblockComponentTypeAdapter extends TypeAdapter<MultiblockComponent>
{
    @Override
    public void write(JsonWriter out, MultiblockComponent value) throws IOException
    {

    }

    @Override
    public MultiblockComponent read(JsonReader in) throws IOException
    {
        int width = 1;
        int height = 1;
        int length = 1;
        int offsetX = 0;
        int offsetY = 0;
        int offsetZ = 0;

        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "width":
                    width = in.nextInt();
                    break;
                case "height":
                    height = in.nextInt();
                    break;
                case "length":
                    length = in.nextInt();
                    break;
                case "offsetX":
                    offsetX = in.nextInt();
                    break;
                case "offsetY":
                    offsetY = in.nextInt();
                    break;
                case "offsetZ":
                    offsetZ = in.nextInt();
                    break;
                case "size":
                    width = height = length = in.nextInt();
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        return new MultiblockComponent(width, height, length, offsetX, offsetY, offsetZ);
    }
}
