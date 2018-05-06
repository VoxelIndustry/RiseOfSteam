package net.ros.common.machine.component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ros.common.machine.IMachineComponent;
import net.ros.common.machine.MachineDescriptor;

@Getter
@Setter
@NoArgsConstructor
public class CraftingComponent implements IMachineComponent
{
    private String recipeCategory;

    private int inventorySize;
    private int inputs;
    private int outputs;

    private float craftingSpeed;

    private String[] inputTanks;
    private String[] outputTanks;

    private MachineDescriptor descriptor;
}
