package net.qbar.common.tile;

import org.apache.commons.lang3.ArrayUtils;

public class CraftingMachineDescriptor extends MachineDescriptor
{
    private final String recipeCategory;

    private final int    inventorySize;
    private final int[]  inputs, outputs, buffers;
    private int[]        iounion;

    private final float  craftingSpeed;

    public CraftingMachineDescriptor(final String name, final String recipeCategory, final int inventorySize,
            final int[] inputs, final int[] outputs, final int[] buffers, final float craftingSpeed,
            final int steamCapacity, final int steamConsumption, final float pressureCapacity,
            final float maxPressureCapacity, final boolean allowOvercharge)
    {
        super(name, steamCapacity, steamConsumption, pressureCapacity, maxPressureCapacity, allowOvercharge);

        this.recipeCategory = recipeCategory;

        this.inventorySize = inventorySize;
        this.inputs = inputs;
        this.outputs = outputs;
        this.buffers = buffers;
        this.iounion = ArrayUtils.addAll(this.inputs, this.outputs);
        this.craftingSpeed = craftingSpeed;
    }

    public CraftingMachineDescriptor(final String name, final String recipeCategory, final int inventorySize,
            final int inputSize, final int outputSize, final float craftingSpeed, final int steamCapacity,
            final int steamConsumption, final float pressureCapacity, final float maxPressureCapacity,
            final boolean allowOvercharge)
    {
        this(name, recipeCategory, inventorySize, new int[inputSize], new int[outputSize], new int[inputSize],
                craftingSpeed, steamCapacity, steamConsumption, pressureCapacity, maxPressureCapacity, allowOvercharge);

        for (int i = 0; i < inputSize; i++)
        {
            this.inputs[i] = i;
            this.buffers[i] = i + inputSize + outputSize;
        }
        for (int i = 0; i < outputSize; i++)
            this.outputs[i] = i + inputSize;

        this.iounion = ArrayUtils.addAll(this.inputs, this.outputs);
    }

    public String getRecipeCategory()
    {
        return this.recipeCategory;
    }

    public int getInventorySize()
    {
        return this.inventorySize;
    }

    public int[] getInputs()
    {
        return this.inputs;
    }

    public int[] getOutputs()
    {
        return this.outputs;
    }

    public int[] getBuffers()
    {
        return this.buffers;
    }

    public int[] getIOUnion()
    {
        return this.iounion;
    }

    public float getCraftingSpeed()
    {
        return this.craftingSpeed;
    }
}
