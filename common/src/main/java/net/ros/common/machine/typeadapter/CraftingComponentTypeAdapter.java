package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ros.common.ROSConstants;
import net.ros.common.machine.component.CraftingComponent;
import net.ros.common.recipe.RecipeHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CraftingComponentTypeAdapter extends TypeAdapter<CraftingComponent>
        implements IMachineComponentTypeAdapter<CraftingComponent>
{
    @Override
    public void write(JsonWriter out, CraftingComponent value)
    {

    }

    @Override
    public Class<CraftingComponent> getComponentClass()
    {
        return CraftingComponent.class;
    }

    @Override
    public CraftingComponent read(JsonReader in) throws IOException
    {
        CraftingComponent component = new CraftingComponent();

        int inventorySize = 0;

        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "category":
                    String category = in.nextString();
                    if (!category.startsWith("ros."))
                        category = "ros." + category;
                    if (RecipeHandler.RECIPES.containsKey(category))
                        component.setRecipeCategory(category);
                    else
                        ROSConstants.LOGGER.error("Unknown recipe category detected while parsing crafter (" +
                                category + ")");
                    break;
                case "inventorySize":
                    inventorySize = in.nextInt();
                    break;
                case "speed":
                    component.setCraftingSpeed((float) in.nextDouble());
                    break;
                case "itemInput":
                    component.setInputs(in.nextInt());
                    break;
                case "itemOutput":
                    component.setOutputs(in.nextInt());
                    break;
                case "tankInput":
                    in.beginArray();
                    List<String> inputs = new ArrayList<>();
                    while (in.hasNext())
                        inputs.add(in.nextString());
                    in.endArray();

                    component.setInputTanks(new String[inputs.size()]);
                    for (int i = 0; i < inputs.size(); i++)
                        component.getInputTanks()[i] = inputs.get(i);
                    break;
                case "tankOutput":
                    in.beginArray();
                    List<String> outputs = new ArrayList<>();
                    while (in.hasNext())
                        outputs.add(in.nextString());
                    in.endArray();

                    component.setOutputTanks(new String[outputs.size()]);
                    for (int i = 0; i < outputs.size(); i++)
                        component.getOutputTanks()[i] = outputs.get(i);
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        if (inventorySize != 0)
            component.setInventorySize(inventorySize);
        else
            component.setInventorySize(component.getInputs() * 2 + component.getOutputs());

        if (component.getInputTanks() == null)
            component.setInputTanks(new String[0]);
        if (component.getOutputTanks() == null)
            component.setOutputTanks(new String[0]);
        return component;
    }
}
