package net.qbar.common.machine.module.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.common.fluid.LimitedTank;
import net.qbar.common.machine.component.CraftingComponent;
import net.qbar.common.machine.component.FluidComponent;
import net.qbar.common.machine.component.SteamComponent;
import net.qbar.common.machine.event.RecipeChangeEvent;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.machine.module.ISerializableModule;
import net.qbar.common.machine.module.ITickableModule;
import net.qbar.common.machine.module.MachineModule;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.ingredient.RecipeIngredient;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.util.ItemUtils;
import org.yggard.hermod.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CraftingModule extends MachineModule implements ITickableModule, ISerializableModule
{
    private static final Function<IModularMachine, Float> DEFAULT_EFFICIENCY = machine ->
            machine.getModule(SteamModule.class).getInternalSteamHandler().getPressure() /
                    machine.getDescriptor().get(SteamComponent.class).getWorkingPressure();

    @Getter
    private final CraftingComponent       crafter;
    @Getter
    private final SteamComponent          steamMachine;
    private       ISteamHandler           steamHandler;
    private       CraftingInventoryModule inventoryModule;

    @Getter
    @Setter
    private float      currentProgress;
    @Getter
    @Setter
    private float      maxProgress;
    @Getter
    private QBarRecipe currentRecipe;

    @Setter
    private EventHandler<RecipeChangeEvent>  onRecipeChange;
    @Setter
    private Function<IModularMachine, Float> efficiencySupplier;

    private List<FluidTank>  inputTanks;
    private List<FluidTank>  outputTanks;
    private List<FluidStack> bufferFluidStacks;

    public CraftingModule(IModularMachine machine)
    {
        super(machine, "CraftingModule");

        this.crafter = machine.getDescriptor().get(CraftingComponent.class);
        this.steamMachine = machine.getDescriptor().get(SteamComponent.class);

        this.efficiencySupplier = DEFAULT_EFFICIENCY;

        this.inputTanks = new ArrayList<>();
        this.outputTanks = new ArrayList<>();
        this.bufferFluidStacks = new ArrayList<>();
    }

    private ISteamHandler getSteamHandler()
    {
        if (steamHandler == null)
            steamHandler = this.getMachine().getModule(SteamModule.class).getInternalSteamHandler();
        return steamHandler;
    }

    private CraftingInventoryModule getInventory()
    {
        if (inventoryModule == null)
            inventoryModule = this.getMachine().getModule(CraftingInventoryModule.class);
        return inventoryModule;
    }

    @Override
    public void tick()
    {
        if (this.isClient())
            return;

        if (this.currentRecipe == null && (!this.getInventory().isInputEmpty() || !this.getInventory().isBufferEmpty()))
        {
            if (this.getSteamHandler().getSteam() >= this.steamMachine.getSteamConsumption())
            {
                if (this.getInventory().isBufferEmpty())
                {
                    final Object[] ingredients = new Object[this.crafter.getBuffers().length
                            + this.crafter.getBufferTanks().length];

                    for (int i = 0; i < this.crafter.getInputs().length; i++)
                        ingredients[i] = this.getInventory().getStackInSlot(this.crafter.getInputs()[i]);
                    for (int i = 0; i < this.crafter.getInputTanks().length; i++)
                        ingredients[this.crafter.getInputTanks().length + i] = this.getInputFluidStack(i);

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.crafter.getRecipeCategory(),
                            ingredients);
                    if (recipe.isPresent() && this.setCurrentRecipe(recipe.get()))
                    {
                        this.setMaxProgress((int) (this.currentRecipe.getTime() / this.getCraftingSpeed()));
                        int i = 0;
                        for (final RecipeIngredient<ItemStack> stack : this.currentRecipe
                                .getRecipeInputs(ItemStack.class))
                        {
                            this.getInventory().decrStackSize(this.crafter.getInputs()[i], stack.getRawIngredient()
                                    .getCount());
                            this.getInventory().setInventorySlotContents(this.crafter.getBuffers()[i],
                                    stack.getRawIngredient().copy());
                            i++;
                        }
                        i = 0;
                        for (final RecipeIngredient<FluidStack> stack : this.currentRecipe
                                .getRecipeInputs(FluidStack.class))
                        {
                            this.inputTanks.get(i).drainInternal(stack.getQuantity(), true);
                            this.bufferFluidStacks.set(i, stack.getRawIngredient().copy());
                            i++;
                        }
                        this.sync();
                    }
                }
                else
                {
                    final Object[] ingredients = new Object[this.crafter.getBuffers().length
                            + this.crafter.getBufferTanks().length];

                    for (int i = 0; i < this.crafter.getBuffers().length; i++)
                        ingredients[i] = this.getInventory().getStackInSlot(this.crafter.getBuffers()[i]);
                    for (int i = 0; i < this.crafter.getBufferTanks().length; i++)
                        ingredients[this.crafter.getBufferTanks().length + i] = this.bufferFluidStacks.get(i);

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.crafter.getRecipeCategory(),
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
        if (this.currentRecipe != null && !this.getInventory().isBufferEmpty())
        {
            if (this.getCurrentProgress() < this.getMaxProgress())
            {
                if (this.getSteamHandler().getSteam() >= this.steamMachine.getSteamConsumption())
                {
                    this.setCurrentProgress(this.getCurrentProgress() + this.getCurrentCraftingSpeed());
                    this.getSteamHandler().drainSteam(this.steamMachine.getSteamConsumption(), true);
                    this.sync();
                }
            }
            else
            {
                int i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeOutputs(ItemStack.class))
                {
                    if (!ItemUtils.canMergeStacks(stack.getRawIngredient(),
                            this.getInventory().getStackInSlot(this.crafter.getOutputs()[i])))
                        return;
                    i++;
                }
                i = 0;
                for (final RecipeIngredient<FluidStack> stack : this.currentRecipe.getRecipeOutputs(FluidStack.class))
                {
                    if (this.outputTanks.get(i).fill(stack.getRawIngredient(), false) == 0)
                        return;
                    i++;
                }

                for (final int buffer : this.crafter.getBuffers())
                    this.getInventory().setInventorySlotContents(buffer, ItemStack.EMPTY);
                for (int j = 0; j < this.crafter.getBufferTanks().length; j++)
                    this.bufferFluidStacks.set(j, null);

                i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.currentRecipe.getRecipeOutputs(ItemStack.class))
                {
                    if (!this.getInventory().getStackInSlot(this.crafter.getOutputs()[i]).isEmpty())
                        this.getInventory().getStackInSlot(this.crafter.getOutputs()[i]).grow(
                                stack.getRawIngredient().getCount());
                    else
                        this.getInventory().setInventorySlotContents(this.crafter.getOutputs()[i],
                                stack.getRawIngredient().copy());
                    i++;
                }
                i = 0;
                for (final RecipeIngredient<FluidStack> stack : this.currentRecipe.getRecipeOutputs(FluidStack.class))
                {
                    this.outputTanks.get(i).fillInternal(stack.getRawIngredient(), true);
                    i++;
                }
                this.setCurrentRecipe(null);
                this.setCurrentProgress(0);
                this.sync();
            }
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.setCurrentProgress(tag.getFloat("currentProgress"));
        this.setMaxProgress(tag.getFloat("maxProgress"));

        if (tag.getInteger("bufferFluidStack") != 0)
        {
            for (int i = 0; i < tag.getInteger("bufferFluidStack"); i++)
            {
                if (!tag.hasKey("bufferFluidStack"))
                {
                    this.bufferFluidStacks.set(i, null);
                    continue;
                }
                this.bufferFluidStacks.set(i,
                        FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("bufferFluidStack" + i)));
            }
        }
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setFloat("currentProgress", this.currentProgress);
        tag.setFloat("maxProgress", this.maxProgress);

        tag.setInteger("bufferFluidStack", this.bufferFluidStacks.size());

        if (!this.bufferFluidStacks.isEmpty())
        {
            int i = -1;
            for (FluidStack stack : this.bufferFluidStacks)
            {
                i++;
                if (stack == null)
                    continue;
                tag.setTag("bufferFluidStack" + i, stack.writeToNBT(new NBTTagCompound()));
            }
        }
        return tag;
    }

    private boolean setCurrentRecipe(QBarRecipe recipe)
    {
        if (this.onRecipeChange != null)
        {
            RecipeChangeEvent event = new RecipeChangeEvent(this.getMachine(), this.currentRecipe, recipe);
            this.onRecipeChange.handle(event);
            if (recipe != null && event.isCancelled())
                return false;
        }
        this.currentRecipe = recipe;
        return true;
    }

    public float getCurrentCraftingSpeed()
    {
        return this.getCraftingSpeed() * this.getEfficiency();
    }

    public float getCraftingSpeed()
    {
        return this.crafter.getCraftingSpeed();
    }

    public float getEfficiency()
    {
        return this.efficiencySupplier.apply(this.getMachine());
    }

    public int getProgressScaled(int scale)
    {
        if (this.currentProgress != 0 && this.maxProgress != 0)
            return (int) (this.currentProgress * scale / this.maxProgress);
        return 0;
    }

    ////////////////////
    // FLUID HANDLING //
    ////////////////////

    public void linkInputTank(String name)
    {
        FluidTank craftTank;

        FluidComponent component = this.getMachine().getDescriptor().get(FluidComponent.class);
        if (component.getTankThrottle(name) != Integer.MAX_VALUE)
            craftTank = new LimitedTank(name, component.getTankCapacity(name), component.getTankThrottle(name));
        else
            craftTank = new FluidTank(component.getTankCapacity(name));

        this.getMachine().getModule(FluidStorageModule.class).replaceFluidHandler(name, craftTank);

        this.inputTanks.add(craftTank);
        this.bufferFluidStacks.add(null);
    }

    public void linkOutputTank(String name)
    {
        FluidComponent component = this.getMachine().getDescriptor().get(FluidComponent.class);
        FluidTank craftTank = new FluidTank(component.getTankCapacity(name));

        this.getMachine().getModule(FluidStorageModule.class).replaceFluidHandler(name, craftTank);

        this.outputTanks.add(craftTank);
    }

    private FluidStack getInputFluidStack(int index)
    {
        return this.inputTanks.get(index).getFluid();
    }
}
