package net.qbar.common.tile;

import java.util.List;
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
import net.qbar.common.recipe.RecipeIngredient;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.util.ItemUtils;

public abstract class TileCraftingMachineBase extends TileInventoryBase
        implements ITileMultiblockCore, ITickable, ISidedInventory, IContainerProvider
{
    private final CraftingMachineDescriptor descriptor;

    private float                           currentProgress;
    private float                           maxProgress;
    private QBarRecipe                      currentRecipe;

    private final SteamTank                 steamTank;

    public TileCraftingMachineBase(final CraftingMachineDescriptor descriptor)
    {
        super(descriptor.getName(), descriptor.getInventorySize());

        this.descriptor = descriptor;

        this.steamTank = new SteamTank(0, descriptor.getSteamCapacity(), descriptor.getMaxPressureCapacity());
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;
        if (this.currentRecipe == null && this.isBufferEmpty() && !this.isInputEmpty())
        {
            if (this.steamTank.getSteam() >= this.descriptor.getSteamConsumption())
            {
                final ItemStack[] stacks = new ItemStack[this.descriptor.getInputs().length];
                for (int i = 0; i < this.descriptor.getInputs().length; i++)
                    stacks[i] = this.getStackInSlot(this.descriptor.getInputs()[i]);

                final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.descriptor.getRecipeCategory(),
                        stacks);
                if (recipe.isPresent())
                {
                    this.currentRecipe = recipe.get();

                    this.setMaxProgress((int) (this.currentRecipe.getTime() / this.getCraftingSpeed()));
                    int i = 0;
                    for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeInputs(ItemStack.class))
                    {
                        this.decrStackSize(this.descriptor.getInputs()[i], stack.getRawIngredient().getCount());
                        this.setInventorySlotContents(this.descriptor.getBuffers()[i], stack.getRawIngredient().copy());
                        i++;
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
                for (final int buffer : this.descriptor.getBuffers())
                    this.setInventorySlotContents(buffer, ItemStack.EMPTY);
                i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeOutputs(ItemStack.class))
                {
                    if (!this.getStackInSlot(this.descriptor.getOutputs()[i]).isEmpty())
                        this.getStackInSlot(this.descriptor.getOutputs()[i]).grow(stack.getRawIngredient().getCount());
                    else
                        this.setInventorySlotContents(this.descriptor.getOutputs()[i], stack.getRawIngredient().copy());
                    i++;
                }
                this.currentRecipe = null;
                this.setCurrentProgress(0);
            }
        }
    }

    public float getCurrentCraftingSpeed()
    {
        return this.getCraftingSpeed() * this.getEfficiency();
    }

    public float getEfficiency()
    {
        return this.steamTank.getPressure() / this.descriptor.getWorkingPressure();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setFloat("currentProgress", this.currentProgress);
        tag.setFloat("maxProgress", this.maxProgress);

        this.steamTank.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.currentProgress = tag.getFloat("currentProgress");
        this.maxProgress = tag.getFloat("maxProgress");

        this.steamTank.readFromNBT(tag);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
        lines.add("Efficiency " + this.getCurrentCraftingSpeed());
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
}
