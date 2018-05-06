package net.ros.common.machine;

import com.google.common.collect.Lists;
import net.ros.common.ROSConstants;
import net.ros.common.multiblock.blueprint.Blueprint;
import net.ros.common.multiblock.blueprint.MultiblockStep;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hjson.JsonValue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.function.Consumer;

public class BlueprintLoader implements Consumer<MachineDescriptor>
{
    @Override
    public void accept(MachineDescriptor blueprint)
    {
        try
        {
            String path = "/assets/" + ROSConstants.MODID + "/multiblock/" + blueprint.getName() + ".hjson";
            if (Machines.class.getResource(path) != null)
            {
                InputStream stream = Machines.class.getResourceAsStream(path);
                ArrayList<MultiblockStep> steps = Lists.newArrayList(Machines.GSON.fromJson(
                        JsonValue.readHjson(IOUtils.toString(stream, StandardCharsets.UTF_8)).toString(),
                        MultiblockStep[].class));
                stream.close();

                for (MultiblockStep step : steps)
                {
                    if (steps.indexOf(step) != 0)
                        step.setParts(
                                ArrayUtils.addAll(step.getParts(), steps.get(steps.indexOf(step) - 1).getParts()));
                }
                blueprint.get(Blueprint.class).setMultiblockSteps(steps);
            }
            else
                ROSConstants.LOGGER.warn("Could not find a blueprint matching the name {}", blueprint.getName());
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}
