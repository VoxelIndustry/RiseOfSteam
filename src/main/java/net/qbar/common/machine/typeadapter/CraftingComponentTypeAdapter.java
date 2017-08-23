package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.qbar.QBar;
import net.qbar.common.machine.CraftingComponent;
import net.qbar.common.recipe.QBarRecipeHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

public class CraftingComponentTypeAdapter extends TypeAdapter<CraftingComponent>
{
    @Override
    public void write(JsonWriter out, CraftingComponent value) throws IOException
    {

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
                    if (!category.startsWith("qbar."))
                        category = "qbar." + category;
                    if (QBarRecipeHandler.RECIPES.containsKey(category))
                        component.setRecipeCategory(category);
                    else
                        QBar.logger.error("Unknown recipe category detected while parsing crafter (" + category + ")");
                    break;
                case "inventorySize":
                    inventorySize = in.nextInt();
                    break;
                case "speed":
                    component.setCraftingSpeed((float) in.nextDouble());
                    break;
                case "itemInput":
                    component.setInputs(new int[in.nextInt()]);
                    component.setBuffers(new int[component.getInputs().length]);
                    break;
                case "itemOutput":
                    component.setOutputs(new int[in.nextInt()]);
                    break;
                case "tankInput":
                    component.setInputTanks(new int[in.nextInt()]);
                    component.setBufferTanks(new int[component.getInputTanks().length]);
                    break;
                case "tankOutput":
                    component.setOutputTanks(new int[in.nextInt()]);
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        component.setIoUnion(ArrayUtils.addAll(component.getInputs(), component.getOutputs()));
        if (inventorySize != 0)
            component.setInventorySize(inventorySize);
        else
            component.setInventorySize(component.getInputs().length +
                    component.getOutputs().length + component.getBuffers().length);
        return component;
    }
}
