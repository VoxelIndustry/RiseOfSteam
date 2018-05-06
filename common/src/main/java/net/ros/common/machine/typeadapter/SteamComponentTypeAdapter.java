package net.ros.common.machine.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.ros.common.machine.component.SteamComponent;
import net.ros.common.steam.SteamUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class SteamComponentTypeAdapter extends TypeAdapter<SteamComponent>
        implements IMachineComponentTypeAdapter<SteamComponent>
{
    @Override
    public void write(JsonWriter out, SteamComponent value)
    {

    }

    @Override
    public Class<SteamComponent> getComponentClass()
    {
        return SteamComponent.class;
    }

    @Override
    public SteamComponent read(JsonReader in) throws IOException
    {
        SteamComponent component = new SteamComponent();
        component.setSafePressureCapacity(-1);

        in.beginObject();
        while (in.hasNext())
        {
            switch (in.nextName())
            {
                case "capacity":
                    component.setSteamCapacity(in.nextInt());
                    break;
                case "consumption":
                    component.setSteamConsumption(in.nextInt());
                    break;
                case "workingPressure":
                    component.setWorkingPressure(parsePressure(in.nextString()));
                    break;
                case "maxPressure":
                    component.setMaxPressureCapacity(parsePressure(in.nextString()));
                    break;
                case "safePressure":
                    component.setSafePressureCapacity(parsePressure(in.nextString()));
                    break;
                case "overcharge":
                    component.setAllowOvercharge(in.nextBoolean());
                    break;
                default:
                    break;
            }
        }
        in.endObject();

        if (component.getSafePressureCapacity() == -1)
            component.setSafePressureCapacity(component.getMaxPressureCapacity());
        return component;
    }

    private float parsePressure(String pressure)
    {
        if (pressure.equals("BASE_PRESSURE"))
            return SteamUtil.BASE_PRESSURE;
        else if (NumberUtils.isNumber(pressure))
            return Float.parseFloat(pressure);
        else if (pressure.contains("x"))
            return Float.parseFloat(pressure.split("x")[0])
                    * SteamUtil.BASE_PRESSURE;
        return 0;
    }
}
