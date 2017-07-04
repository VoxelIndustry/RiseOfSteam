package net.qbar.common.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.ingredient.RecipeIngredient;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.util.ItemUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;

public abstract class TileCraftingMachineBase extends TileInventoryBase
        implements ITileMultiblockCore, ITickable, ISidedInventory, IContainerProvider
{
    private final CraftingMachineDescriptor descriptor;

    private float      currentProgress;
    private float      maxProgress;
    private QBarRecipe currentRecipe;

    private final SteamTank steamTank;

    private final FluidTank[] inputTanks, outputTanks, bufferTanks;

    public TileCraftingMachineBase(final CraftingMachineDescriptor descriptor)
    {
        super(descriptor.getName(), descriptor.getInventorySize());

        this.descriptor = descriptor;

        this.steamTank = new SteamTank(0, descriptor.getSteamCapacity(), descriptor.getMaxPressureCapacity());

        if (descriptor.getInputTanks().length > 0)
        {
            this.inputTanks = new FluidTank[descriptor.getInputTanks().length];
            this.bufferTanks = new FluidTank[descriptor.getInputTanks().length];

            int i = 0;
            for (int size : descriptor.getInputTanks())
            {
                int index = i;
                this.inputTanks[i] = new FilteredFluidTank(size, fluidStack -> QBarRecipeHandler
                        .inputMatchWithoutCount(descriptor.getRecipeCategory(), index, fluidStack));
                this.bufferTanks[i] = new FluidTank(size);
                i++;
            }
        }
        else
        {
            this.inputTanks = new FluidTank[0];
            this.bufferTanks = new FluidTank[0];
        }

        if (descriptor.getOutputTanks().length > 0)
        {
            this.outputTanks = new FluidTank[descriptor.getOutputTanks().length];

            int i = 0;
            for (int size : descriptor.getOutputTanks())
            {
                this.outputTanks[i] = new FluidTank(size);
                i++;
            }
        }
        else
            this.outputTanks = new FluidTank[0];
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (this.currentRecipe == null && (!this.isInputEmpty() || !this.isBufferEmpty()))
        {
            if (this.steamTank.getSteam() >= this.descriptor.getSteamConsumption())
            {
                if (this.isBufferEmpty())
                {
                    final Object[] ingredients = new Object[this.descriptor.getBuffers().length
                            + this.descriptor.getBufferTanks().length + this.getCustomData().length];

                    for (int i = 0; i < this.descriptor.getInputs().length; i++)
                        ingredients[i] = this.getStackInSlot(this.descriptor.getInputs()[i]);
                    for (int i = 0; i < this.descriptor.getInputTanks().length; i++)
                        ingredients[this.descriptor.getInputTanks().length + i] = this.getInputFluidStack(i);
                    for (int i = 0; i < this.getCustomData().length; i++)
                        ingredients[this.descriptor.getInputs().length + this.descriptor.getInputTanks().length
                                + i] = this.getCustomData()[i];

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.descriptor.getRecipeCategory(),
                            ingredients);
                    if (recipe.isPresent() && this.acceptRecipe(recipe.get()))
                    {
                        this.setCurrentRecipe(recipe.get());

                        this.setMaxProgress((int) (this.currentRecipe.getTime() / this.getCraftingSpeed()));
                        int i = 0;
                        for (final RecipeIngredient<ItemStack> stack : this.currentRecipe
                                .getRecipeInputs(ItemStack.class))
                        {
                            this.decrStackSize(this.descriptor.getInputs()[i], stack.getRawIngredient().getCount());
                            this.setInventorySlotContents(this.descriptor.getBuffers()[i],
                                    stack.getRawIngredient().copy());
                            i++;
                        }
                        i = 0;
                        for (final RecipeIngredient<FluidStack> stack : this.currentRecipe
                                .getRecipeInputs(FluidStack.class))
                        {
                            this.inputTanks[i].drainInternal(stack.getQuantity(), true);
                            this.bufferTanks[i].fillInternal(stack.getRawIngredient().copy(), true);
                            i++;
                        }
                        this.sync();
                    }
                }
                else
                {
                    final Object[] ingredients = new Object[this.descriptor.getBuffers().length
                            + this.descriptor.getBufferTanks().length + this.getCustomData().length];

                    for (int i = 0; i < this.descriptor.getBuffers().length; i++)
                        ingredients[i] = this.getStackInSlot(this.descriptor.getBuffers()[i]);
                    for (int i = 0; i < this.descriptor.getBufferTanks().length; i++)
                        ingredients[this.descriptor.getBufferTanks().length + i] = this.getBufferFluidStack(i);
                    for (int i = 0; i < this.getCustomData().length; i++)
                        ingredients[this.descriptor.getBuffers().length + this.descriptor.getBufferTanks().length
                                + i] = this.getCustomData()[i];

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.descriptor.getRecipeCategory(),
                            ingredients);
                    if (recipe.isPresent())
                    {
                        this.setCurrentRecipe(recipe.get());
                        this.setMaxProgress((int) (this.currentRecipe.getTime() / this.getCraftingSpeed()));

                        this.sync();
                    }
                }
            }
        }
        if (this.currentRecipe != null && !this.isBufferEmpty())
        {
            if (this.getCurrentProgress() < this.getMaxProgress())
            {
                if (this.steamTank.getSteam() >= this.descriptor.getSteamConsumption())
                {
                    this.setCurrentProgress(this.getCurrentProgress() + this.getCurrentCraftingSpeed());
                    this.useSteam(this.descriptor.getSteamConsumption());
                    this.sync();
                }
            }
            else
            {
                int i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeOutputs(ItemStack.class))
                {
                    if (!ItemUtils.canMergeStacks(stack.getRawIngredient(),
                            this.getStackInSlot(this.descriptor.getOutputs()[i])))
                        return;
                    i++;
                }
                i = 0;
                for (final RecipeIngredient<FluidStack> stack : this.currentRecipe.getRecipeOutputs(FluidStack.class))
                {
                    if (this.outputTanks[i].fill(stack.getRawIngredient(), false) == 0)
                        return;
                    i++;
                }

                for (final int buffer : this.descriptor.getBuffers())
                    this.setInventorySlotContents(buffer, ItemStack.EMPTY);
                for (int j = 0; j < this.descriptor.getBufferTanks().length; j++)
                    this.setBufferFluidStack(j, null);

                i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeOutputs(ItemStack.class))
                {
                    if (!this.getStackInSlot(this.descriptor.getOutputs()[i]).isEmpty())
                        this.getStackInSlot(this.descriptor.getOutputs()[i]).grow(stack.getRawIngredient().getCount());
                    else
                        this.setInventorySlotContents(this.descriptor.getOutputs()[i], stack.getRawIngredient().copy());
                    i++;
                }
                i = 0;
                for (final RecipeIngredient<FluidStack> stack : this.currentRecipe.getRecipeOutputs(FluidStack.class))
                {
                    this.outputTanks[i].fillInternal(stack.getRawIngredient(), true);
                    i++;
                }
                this.setCurrentRecipe(null);
                this.setCurrentProgress(0);
                this.sync();
            }
        }
    }

    protected boolean acceptRecipe(QBarRecipe recipe)
    {
        return true;
    }

    protected void setCurrentRecipe(QBarRecipe recipe)
    {
        this.currentRecipe = recipe;
        this.onRecipeChange();
    }

    protected void onRecipeChange()
    {

    }

    public Object[] getCustomData()
    {
        return new Object[0];
    }

    public float getCurrentCraftingSpeed()
    {
        return this.getCraftingSpeed() * this.getEfficiency();
    }

    public float getEfficiency()
    {
        return this.steamTank.getPressure() / this.descriptor.getWorkingPressure();
    }

    public QBarRecipe getCurrentRecipe()
    {
        return this.currentRecipe;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("currentProgress", this.currentProgress);
        tag.setFloat("maxProgress", this.maxProgress);

        this.steamTank.writeToNBT(tag);

        int i;
        for (i = 0; i < this.inputTanks.length; i++)
            tag.setTag("inputTank" + i, this.inputTanks[i].writeToNBT(new NBTTagCompound()));
        tag.setInteger("inputTankCount", i);
        for (i = 0; i < this.bufferTanks.length; i++)
            tag.setTag("bufferTank" + i, this.bufferTanks[i].writeToNBT(new NBTTagCompound()));
        tag.setInteger("bufferTankCount", i);
        for (i = 0; i < this.outputTanks.length; i++)
            tag.setTag("outputTank" + i, this.outputTanks[i].writeToNBT(new NBTTagCompound()));
        tag.setInteger("outputTankCount", i);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentProgress = tag.getFloat("currentProgress");
        this.maxProgress = tag.getFloat("maxProgress");

        this.steamTank.readFromNBT(tag);

        for (int i = 0; i < tag.getInteger("inputTankCount"); i++)
            this.inputTanks[i].readFromNBT(tag.getCompoundTag("inputTank" + i));
        for (int i = 0; i < tag.getInteger("bufferTankCount"); i++)
            this.bufferTanks[i].readFromNBT(tag.getCompoundTag("bufferTank" + i));
        for (int i = 0; i < tag.getInteger("outputTankCount"); i++)
            this.outputTanks[i].readFromNBT(tag.getCompoundTag("outputTank" + i));
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
        lines.add("Crafting Speed " + this.getCurrentCraftingSpeed());
        lines.add("Efficiency " + this.getEfficiency());
    }

    @Override
    public int[] getSlotsForFace(final EnumFacing side)
    {
        if (side != EnumFacing.DOWN)
        {
            if (side == EnumFacing.UP)
                return this.descriptor.getInputs();
            return this.descriptor.getIOUnion();
        }
        return this.descriptor.getOutputs();
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
        if (ArrayUtils.contains(this.descriptor.getInputs(), index))
            return this.isItemValidForSlot(index, itemStackIn);
        return false;
    }

    @Override
    public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction)
    {
        return ArrayUtils.contains(this.descriptor.getOutputs(), index);
    }

    public float getCurrentProgress()
    {
        return this.currentProgress;
    }

    public void setCurrentProgress(final float currentProgress)
    {
        this.currentProgress = currentProgress;
    }

    public float getMaxProgress()
    {
        return this.maxProgress;
    }

    public void setMaxProgress(final float maxProgress)
    {
        this.maxProgress = maxProgress;
    }

    public int getProgressScaled(final int scale)
    {
        if (this.currentProgress != 0 && this.maxProgress != 0)
            return (int) (this.currentProgress * scale / this.maxProgress);
        return 0;
    }

    public float getCraftingSpeed()
    {
        return this.descriptor.getCraftingSpeed();
    }

    public boolean isBufferEmpty()
    {
        for (int i = 0; i < this.descriptor.getBuffers().length; i++)
        {
            if (!this.getStackInSlot(this.descriptor.getBuffers()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isInputEmpty()
    {
        for (int i = 0; i < this.descriptor.getInputs().length; i++)
        {
            if (!this.getStackInSlot(this.descriptor.getInputs()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public boolean isOutputEmpty()
    {
        for (int i = 0; i < this.descriptor.getOutputs().length; i++)
        {
            if (!this.getStackInSlot(this.descriptor.getOutputs()[i]).isEmpty())
                return false;
        }
        return true;
    }

    public int useSteam(final int amount)
    {
        return this.steamTank.drainSteam(amount, true);
    }

    public CraftingMachineDescriptor getDescriptor()
    {
        return this.descriptor;
    }

    public SteamTank getSteamTank()
    {
        return this.steamTank;
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        return this.getCapability(capability, BlockPos.ORIGIN, facing);
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }

    public FluidStack getInputFluidStack(int index)
    {
        return this.inputTanks[index].getFluid();
    }

    public FluidStack getBufferFluidStack(int index)
    {
        return this.bufferTanks[index].getFluid();
    }

    public FluidStack getOutputFluidStack(int index)
    {
        return this.outputTanks[index].getFluid();
    }

    public void setInputFluidStack(int index, FluidStack fluid)
    {
        this.inputTanks[index].setFluid(fluid);
    }

    public void setBufferFluidStack(int index, FluidStack fluid)
    {
        this.bufferTanks[index].setFluid(fluid);
    }

    public void setOutputFluidStack(int index, FluidStack fluid)
    {
        this.outputTanks[index].setFluid(fluid);
    }

    public FluidTank[] getInputTanks()
    {
        return inputTanks;
    }

    public FluidTank[] getOutputTanks()
    {
        return outputTanks;
    }

    public FluidTank[] getBufferTanks()
    {
        return bufferTanks;
    }
}
