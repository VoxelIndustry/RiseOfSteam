package net.qbar.common.tile;

import org.apache.commons.lang3.ArrayUtils;

public class CraftingMachineDescriptor extends MachineDescriptor
{
    public static class Builder
    {
        private final String name;
        private       String recipeCategory;
        private       int    inventorySize, inputSize, outputSize;
        private int[] inputTanks, outputTanks;
        private float craftingSpeed;

        private int   steamCapacity;
        private float workingPressure, maxPressureCapacity;
        private int     steamConsumption;
        private boolean allowOvercharge;

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder recipe(String recipeCategory, float craftingSpeed)
        {
            this.recipeCategory = recipeCategory;
            this.craftingSpeed = craftingSpeed;
            return this;
        }

        public Builder inventory(int inventorySize, int inputSize, int outputSize)
        {
            this.inventorySize = inventorySize;
            this.inputSize = inputSize;
            this.outputSize = outputSize;
            return this;
        }

        public Builder inputTanks(int... capacity)
        {
            this.inputTanks = capacity;
            return this;
        }

        public Builder outputTanks(int... capacity)
        {
            this.outputTanks = capacity;
            return this;
        }

        public Builder steam(int steamCapacity, int steamConsumption, float workingPressure, float maxPressureCapacity,
                             boolean allowOvercharge)
        {
            this.steamCapacity = steamCapacity;
            this.steamConsumption = steamConsumption;
            this.workingPressure = workingPressure;
            this.maxPressureCapacity = maxPressureCapacity;
            this.allowOvercharge = allowOvercharge;
            return this;
        }

        public CraftingMachineDescriptor create()
        {
            return new CraftingMachineDescriptor(this.name, this.recipeCategory, this.inventorySize, this.inputSize,
                    this.outputSize, this.craftingSpeed, this.steamCapacity, this.steamConsumption,
                    this.workingPressure, this.maxPressureCapacity, this.allowOvercharge, this.inputTanks != null ? this.inputTanks : new int[0],
                    this.outputTanks != null ? this.outputTanks : new int[0]);
        }
    }

    private final String recipeCategory;

    private final int   inventorySize;
    private final int[] inputs, outputs, buffers;
    private int[] iounion;

    private final float craftingSpeed;

    private final int[] inputTanks, outputTanks, bufferTanks;

    private CraftingMachineDescriptor(final String name, final String recipeCategory, final int inventorySize,
                                      final int[] inputs, final int[] outputs, final int[] buffers, final float craftingSpeed,
                                      final int steamCapacity, final int steamConsumption, final float workingPressure,
                                      final float maxPressureCapacity, final boolean allowOvercharge, int[] inputTanks, int[] outputTanks)
    {
        super(name, steamCapacity, workingPressure, maxPressureCapacity,steamConsumption, allowOvercharge);

        this.recipeCategory = recipeCategory;

        this.inventorySize = inventorySize;
        this.inputs = inputs;
        this.outputs = outputs;
        this.buffers = buffers;
        this.iounion = ArrayUtils.addAll(this.inputs, this.outputs);
        this.craftingSpeed = craftingSpeed;

        this.inputTanks = inputTanks;
        this.outputTanks = outputTanks;
        this.bufferTanks = inputTanks;
    }

    private CraftingMachineDescriptor(final String name, final String recipeCategory, final int inventorySize,
                                      final int inputSize, final int outputSize, final float craftingSpeed, final int steamCapacity,
                                      final int steamConsumption, final float pressureCapacity, final float maxPressureCapacity,
                                      final boolean allowOvercharge, int[] inputTanks, int[] outputTanks)
    {
        this(name, recipeCategory, inventorySize, new int[inputSize], new int[outputSize], new int[inputSize],
                craftingSpeed, steamCapacity, steamConsumption, pressureCapacity, maxPressureCapacity, allowOvercharge,
                inputTanks, outputTanks);

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

    public int[] getInputTanks()
    {
        return inputTanks;
    }

    public int[] getOutputTanks()
    {
        return outputTanks;
    }

    public int[] getBufferTanks()
    {
        return bufferTanks;
    }
}
