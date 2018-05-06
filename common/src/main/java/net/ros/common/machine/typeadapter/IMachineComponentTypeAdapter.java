package net.ros.common.machine.typeadapter;

import com.google.gson.stream.JsonReader;
import net.ros.common.machine.IMachineComponent;

import java.io.IOException;

public interface IMachineComponentTypeAdapter<T extends IMachineComponent>
{
    Class<T> getComponentClass();

    T read(JsonReader in) throws IOException;
}
