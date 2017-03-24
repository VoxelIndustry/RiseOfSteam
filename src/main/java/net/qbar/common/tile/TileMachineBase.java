package net.qbar.common.tile;

import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.util.ItemUtils;

public abstract class TileMachineBase extends TileInventoryBase
        implements ITileMultiblockCore, ITickable, ISidedInventory, IContainerProvider
{
    private final String recipeID;
    private final float  craftingSpeed;

    private final int[]  inputs, outputs, buffers;

    private int[]        iounion;

    private int          currentProgress;
    private int          maxProgress;
    private QBarRecipe   currentRecipe;

    public TileMachineBase(final String name, final String recipeID, final float craftingSpeed, final int inventorySize,
            final int[] inputs, final int[] outputs, final int[] buffers)
    {
        super(name, inventorySize);

        this.recipeID = recipeID;

        this.inputs = inputs;
        this.outputs = outputs;
        this.buffers = buffers;

        this.iounion = ArrayUtils.addAll(inputs, outputs);
        this.craftingSpeed = craftingSpeed;
    }

    public TileMachineBase(final String name, final String recipeID, final float craftingSpeed, final int inventorySize,
            final int inputSize, final int outputSize)
    {
        this(name, recipeID, craftingSpeed, inventorySize, new int[inputSize], new int[outputSize], new int[inputSize]);

        for (int i = 0; i < inputSize; i++)
        {
            this.inputs[i] = i;
            this.buffers[i] = i + inputSize + outputSize;
        }
        for (int i = 0; i < outputSize; i++)
            this.outputs[i] = i + inputSize;

        this.iounion = ArrayUtils.addAll(this.inputs, this.outputs);
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
        if (this.currentRecipe == null && this.isBufferEmpty() && !this.isInputEmpty())
        {
            final ItemStack[] stacks = new ItemStack[this.inputs.length];
            for (int i = 0; i < this.inputs.length; i++)
                stacks[i] = this.getStackInSlot(this.inputs[i]);

            final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.recipeID, stacks);
            if (recipe.isPresent())
            {
                this.currentRecipe = recipe.get();

                this.setMaxProgress((int) (this.currentRecipe.getTime() / this.getCraftingSpeed()));
                int i = 0;
                for (final ItemStack stack : this.currentRecipe.getRecipeInputs())
                {
                    this.decrStackSize(this.inputs[i], stack.getCount());
                    this.setInventorySlotContents(this.buffers[i], stack.copy());
                    i++;
                }
            }
        }
        if (this.currentRecipe != null && !this.isBufferEmpty())
        {
            if (this.getCurrentProgress() < this.getMaxProgress())
                this.setCurrentProgress(this.getCurrentProgress() + 1);
            else
            {
                int i = 0;
                for (final ItemStack stack : this.currentRecipe.getRecipeOutputs())
                {
                    if (!ItemUtils.canMergeStacks(stack, this.getStackInSlot(this.outputs[i])))
                        return;
                    i++;
                }
                for (final int buffer : this.buffers)
                    this.setInventorySlotContents(buffer, ItemStack.EMPTY);
                i = 0;
                for (final ItemStack stack : this.currentRecipe.getRecipeOutputs())
                {
                    if (!this.getStackInSlot(this.outputs[i]).isEmpty())
                        this.getStackInSlot(this.outputs[i]).grow(stack.getCount());
                    else
                        this.setInventorySlotContents(this.outputs[i], stack.copy());
                    i++;
                }
                this.currentRecipe = null;
                this.setCurrentProgress(0);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("currentProgress", this.currentProgress);
        tag.setInteger("maxProgress", this.maxProgress);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentProgress = tag.getInteger("currentProgress");
        this.maxProgress = tag.getInteger("maxProgress");
    }

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        if (side != EnumFacing.DOWN)
        {
            if (side == EnumFacing.UP)
                return this.inputs;
            return this.iounion;
        }
        return this.outputs;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction)
    {
        if (ArrayUtils.contains(this.inputs, index))
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        return ArrayUtils.contains(this.outputs, index);
    }

    public int getCurrentProgress()
    {
        return this.currentProgress;
    }

    public void setCurrentProgress(final int currentProgress)
    {
        this.currentProgress = currentProgress;
    }

    public int getMaxProgress()
    {
        return this.maxProgress;
    }

    public void setMaxProgress(final int maxProgress)
    {
        this.maxProgress = maxProgress;
    }

    public int getProgressScaled(final int scale)
    {
        if (this.currentProgress != 0 && this.maxProgress != 0)
            return this.currentProgress * scale / this.maxProgress;
        return 0;
    }

    public float getCraftingSpeed()
    {
        return this.craftingSpeed;
    }

    public boolean isBufferEmpty()
    {
        for (int i = 0; i < this.buffers.length; i++)
        {
            if (!this.getStackInSlot(this.buffers[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isInputEmpty()
    {
        for (int i = 0; i < this.inputs.length; i++)
        {
            if (!this.getStackInSlot(this.inputs[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isOutputEmpty()
    {
        for (int i = 0; i < this.outputs.length; i++)
        {
            if (!this.getStackInSlot(this.outputs[i]).isEmpty())
                return false;
        }
        return true;
    }
}
