package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import net.qbar.QBar;
import net.qbar.common.multiblock.blueprint.Blueprint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlueprintComponentTypeAdapter extends TypeAdapter<Blueprint>
{
    @Override
    public void write(JsonWriter out, Blueprint value) throws IOException
    {

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
        blueprint.addStep(time, stacks.toArray(new ItemStack[stacks.size()]));
    }

    private ItemStack parseItem(String stack)
    {
        Item item;
        int quantity = 1;

        if (Character.isDigit(stack.charAt(0)))
        {
            quantity = Integer.parseInt(stack.split("x")[0]);
            stack = stack.substring(stack.indexOf('x') + 1, stack.length());
        }
        if (stack.startsWith("("))
        {
            NonNullList<ItemStack> ores = OreDictionary.getOres(stack.substring(1, stack.length() - 1));
            if (ores.isEmpty())
            {
                QBar.logger.error("Unknown oredict entry detected while reading a blueprint step (" + stack + ")!");
                return ItemStack.EMPTY;
            }
            item = ores.get(0).getItem();
        }
        else
            item = Item.getByNameOrId(stack);

        if (item == null || item == Items.AIR)
        {
            QBar.logger.error("Unknown item detected while reading a blueprint step (" + stack + ")!");
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, quantity);
    }
}
