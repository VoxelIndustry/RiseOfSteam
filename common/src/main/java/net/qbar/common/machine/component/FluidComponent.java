package net.qbar.common.machine.component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class FluidComponent implements IMachineComponent
{
    private MachineDescriptor descriptor;

    private HashMap<String, Pair<Integer, Integer>> tanks = new HashMap<>();

    public Integer getTankCapacity(String name)
    {
        return this.tanks.get(name).getKey();
    }

    public Integer getTankThrottle(String name)
    {
        return this.tanks.get(name).getValue();
    }
}
