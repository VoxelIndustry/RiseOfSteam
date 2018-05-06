package net.ros.common.machine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
public class MachineDescriptor
{
    private String name;

    private EMachineTier                         tier;
    private EMachineType                         type;
    private HashMap<Class<?>, IMachineComponent> components;

    public MachineDescriptor(String name, EMachineTier tier, EMachineType type)
    {
        this.name = name;
        this.tier = tier;
        this.type = type;

        this.components = new HashMap<>();
    }

    public boolean has(Class<?> componentType)
    {
        return this.components.containsKey(componentType);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> componentType)
    {
        return (T) this.components.get(componentType);
    }

    public MachineDescriptor component(IMachineComponent component)
    {
        component.setDescriptor(this);
        this.components.put(component.getClass(), component);
        return this;
    }
}