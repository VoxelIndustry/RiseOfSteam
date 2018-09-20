package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.ROSConstants;
import net.ros.common.multiblock.blueprint.Blueprint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlueprintComponentTypeAdapter extends TypeAdapter<Blueprint>
        implements IMachineComponentTypeAdapter<Blueprint>
{
    @Override
    public void write(JsonWriter out, Blueprint value)
    {

    }

    @Override
    public Class<Blueprint> getComponentClass()
    {
        return Blueprint.class;
    }

    @Override
    public Blueprint read(JsonReader in) throws IOException
    {
        Blueprint component = new Blueprint();

        in.beginArray();
        while (in.hasNext())
        {
            in.beginObject();
            parseStep(in, component);
            in.endObject();
        }
        in.endArray();

        return component;
    }

    private void parseStep(JsonReader in, Blueprint blueprint) throws IOException
    {
        List<ItemStack> stacks = new ArrayList<>();
        int time = 0;

        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "time":
                    time = in.nextInt();
                    break;
                case "item":
                    stacks.add(parseItem(in.nextString()));
                    break;
                case "items":
                    in.beginArray();
                    while (in.hasNext())
                        stacks.add(parseItem(in.nextString()));
                    in.endArray();
                    break;
                default:
                    break;
            }
        }
        blueprint.addStep(time, stacks.toArray(new ItemStack[0]));
    }

    private ItemStack parseItem(String stack)
    {
        ItemStack item;
        int quantity = 1;

        if (Character.isDigit(stack.charAt(0)))
        {
            quantity = Integer.parseInt(stack.split("x")[0]);
            stack = stack.substring(stack.indexOf('x') + 1, stack.length());
        }
        if (stack.startsWith("("))
        {
            stack = stack.substring(1, stack.length() - 1);
            NonNullList<ItemStack> ores = OreDictionary.getOres(stack);
            if (ores.isEmpty())
            {
                ROSConstants.LOGGER.error("Unknown oredict entry detected while reading a blueprint step (" + stack
                        + ")!");
                return ItemStack.EMPTY;
            }
            item = ores.get(0).copy();
        }
        else
            item = new ItemStack(Item.getByNameOrId(stack));

        if (item.isEmpty())
        {
            ROSConstants.LOGGER.error("Unknown item detected while reading a blueprint step (" + stack + ")!");
            return ItemStack.EMPTY;
        }
        item.setCount(quantity);
        return item;
    }
}
