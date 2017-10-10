package net.qbar.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.CraftingComponent;
import net.qbar.common.recipe.QBarRecipeHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CraftingComponentTypeAdapter extends TypeAdapter<CraftingComponent>
        implements IMachineComponentTypeAdapter<CraftingComponent>
{
    @Override
    public void write(JsonWriter out, CraftingComponent value) throws IOException
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
                    if (!category.startsWith("qbar."))
                        category = "qbar." + category;
                    if (QBarRecipeHandler.RECIPES.containsKey(category))
                        component.setRecipeCategory(category);
                    else
                        QBarConstants.LOGGER.error("Unknown recipe category detected while parsing crafter (" +
                                category + ")");
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
                    in.beginArray();
                    List<Integer> inputs = new ArrayList<>();
                    while (in.hasNext())
                        inputs.add(in.nextInt());
                    in.endArray();

                    component.setInputTanks(new int[inputs.size()]);
                    for (int i = 0; i < inputs.size(); i++)
                        component.getInputTanks()[i] = inputs.get(i);
                    component.setBufferTanks(ArrayUtils.clone(component.getInputTanks()));
                    break;
                case "tankOutput":
                    in.beginArray();
                    List<Integer> outputs = new ArrayList<>();
                    while (in.hasNext())
                        outputs.add(in.nextInt());
                    in.endArray();

                    component.setOutputTanks(new int[outputs.size()]);
                    for (int i = 0; i < outputs.size(); i++)
                        component.getOutputTanks()[i] = outputs.get(i);
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        for (int i = 0; i < component.getInputs().length; i++)
        {
            component.getInputs()[i] = i;
            component.getBuffers()[i] = i + component.getInputs().length + component.getOutputs().length;
        }
        for (int i = 0; i < component.getOutputs().length; i++)
            component.getOutputs()[i] = i + component.getInputs().length;
        component.setIoUnion(ArrayUtils.addAll(component.getInputs(), component.getOutputs()));

        if (inventorySize != 0)
            component.setInventorySize(inventorySize);
        else
            component.setInventorySize(component.getInputs().length +
                    component.getOutputs().length + component.getBuffers().length);

        if (component.getInputTanks() == null)
            component.setInputTanks(new int[0]);
        if (component.getOutputTanks() == null)
            component.setOutputTanks(new int[0]);
        if (component.getBufferTanks() == null)
            component.setBufferTanks(new int[0]);
        return component;
    }
}
