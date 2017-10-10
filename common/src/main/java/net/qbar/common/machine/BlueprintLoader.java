package net.qbar.common.machine;

import com.google.common.collect.Lists;
import net.qbar.common.QBarConstants;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.multiblock.blueprint.MultiblockStep;
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
            String path = "/assets/" + QBarConstants.MODID + "/multiblock/" + blueprint.getName() + ".hjson";
            if (QBarMachines.class.getResource(path) != null)
            {
                InputStream stream = QBarMachines.class.getResourceAsStream(path);
                ArrayList<MultiblockStep> steps = Lists.newArrayList(QBarMachines.GSON.fromJson(
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
                QBarConstants.LOGGER.warn("Could not find a blueprint matching the name {}", blueprint.getName());
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}
