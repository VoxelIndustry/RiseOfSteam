package net.qbar.common.machine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CraftingComponent implements IMachineComponent
{
    private String recipeCategory;

    private int   inventorySize;
    private int[] inputs;
    private int[] outputs;
    private int[] buffers;
    private int[] ioUnion;

    private float craftingSpeed;

    private int[] inputTanks;
    private int[] outputTanks;
    private int[] bufferTanks;

    private MachineDescriptor descriptor;
}
