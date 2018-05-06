package net.ros.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.ros.common.ROSConstants;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.recipe.MaterialShape;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.RecipeBase;
import net.ros.common.recipe.RecipeHandler;
import net.ros.common.recipe.type.AlloyRecipe;
import net.ros.common.recipe.type.MeltRecipe;
import net.ros.common.util.ItemUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class TileAlloyCauldron extends TileTickingModularMachine implements IContainerProvider, IActionReceiver
{
    private final FluidTank inputTankLeft;
    private final FluidTank inputTankRight;
    private final FluidTank outputTank;

    private final float maxHeat;
    @Setter
    private       float heat;
    @Setter
    private       float currentBurnTime;
    @Setter
    private       float maxBurnTime;

    private MeltRecipe currentRecipe;
    @Setter
    private float      meltProgress;
    @Setter
    private float      castProgress;
    private float      castSpeed;

    /*
     * 0 = Input slot
     * 1 = Buffer slot
     * 2 = Output Buffer slot
     * 3 = Output Slot
     * 4 = Fuel slot
     */
    public TileAlloyCauldron()
    {
        super(Machines.ALLOY_CAULDRON);
        this.inputTankLeft = new FluidTank(12 * Fluid.BUCKET_VOLUME);
        this.inputTankRight = new FluidTank(12 * Fluid.BUCKET_VOLUME);
        this.outputTank = new FluidTank(24 * Fluid.BUCKET_VOLUME);
        this.maxHeat = 1500;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 5));
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");
        this.heatLogic(inventory);
        this.meltLogic(inventory);
        this.castLogic(inventory);
    }

    private void heatLogic(InventoryHandler inventory)
    {
        if (this.heat > this.getMinimumTemp())
            this.heat -= 0.5f;
        if (this.heat < this.getMinimumTemp())
            this.heat = this.getMinimumTemp();
        if (this.heat >= this.maxHeat)
            return;

        if (!inventory.getStackInSlot(4).isEmpty() && this.maxBurnTime == 0)
            this.maxBurnTime = TileEntityFurnace.getItemBurnTime(inventory.extractItem(4, 1, false));

        if (this.maxBurnTime != 0)
        {
            this.currentBurnTime++;
            this.heat++;
        }

        if (this.currentBurnTime >= this.maxBurnTime)
        {
            this.currentBurnTime = 0;
            this.maxBurnTime = 0;
        }
    }

    private ItemStack cachedIngredient = ItemStack.EMPTY;

    private void meltLogic(InventoryHandler inventory)
    {
        if (this.heat == this.getMinimumTemp() || inventory.isEmpty())
            return;

        if (!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() &&
                !ItemUtils.deepEquals(cachedIngredient, inventory.getStackInSlot(0)))
        {
            RecipeHandler.getRecipe(RecipeHandler.MELTING_UID, inventory.getStackInSlot(0))
                    .ifPresent(recipe ->
                    {
                        if (this.acceptMelt((MeltRecipe) recipe))
                            this.currentRecipe = (MeltRecipe) recipe;
                    });
            this.cachedIngredient = inventory.getStackInSlot(0).copy();
        }

        if (this.currentRecipe != null && this.currentRecipe.getLowMeltingPoint() <= this.heat &&
                fillTanks(this.currentRecipe.getOutput(), false))
        {
            if (inventory.getStackInSlot(1).isEmpty())
            {
                inventory.setStackInSlot(1, inventory.extractItem(0, 1, false));
                this.cachedIngredient = ItemStack.EMPTY;
            }
            float efficiency = (this.heat - this.currentRecipe.getLowMeltingPoint()) /
                    (this.currentRecipe.getHighMeltingPoint() - this.currentRecipe.getLowMeltingPoint());
            this.meltProgress += ((float) 1 / this.currentRecipe.getTime()) * efficiency;

            if (this.meltProgress > 1)
            {
                this.fillTanks(this.currentRecipe.getOutput(), true);
                this.currentRecipe = null;
                inventory.setStackInSlot(1, ItemStack.EMPTY);
                this.meltProgress = 0;
                this.sync();
            }
        }
    }

    private AlloyRecipe                         cachedAlloyRecipe;
    private MutablePair<FluidStack, FluidStack> cachedAlloyIngredients = new MutablePair<>();

    private void alloyLogic()
    {
        if (this.inputTankLeft.getFluidAmount() == 0 || this.inputTankRight.getFluidAmount() == 0)
            return;

        if (this.cachedAlloyRecipe == null &&
                (!this.inputTankLeft.getFluid().isFluidStackIdentical(cachedAlloyIngredients.getLeft()) ||
                        !this.inputTankRight.getFluid().isFluidStackIdentical(cachedAlloyIngredients.getRight())))
        {
            Optional<RecipeBase> recipe = RecipeHandler.getRecipe(RecipeHandler.ALLOY_UID,
                    this.inputTankLeft.getFluid(), this.inputTankRight.getFluid());
            if (!recipe.isPresent())
                recipe = RecipeHandler.getRecipe(RecipeHandler.ALLOY_UID,
                        this.inputTankRight.getFluid(), this.inputTankLeft.getFluid());

            recipe.ifPresent(alloyRecipe -> cachedAlloyRecipe = (AlloyRecipe) alloyRecipe);

            cachedAlloyIngredients.setLeft(this.inputTankLeft.getFluid().copy());
            cachedAlloyIngredients.setRight(this.inputTankRight.getFluid().copy());
        }

        if (cachedAlloyRecipe != null)
        {
            int alloyAmount = this.cachedAlloyRecipe.getInputs().get(0).match(this.inputTankLeft.getFluid()) ?
                    this.inputTankLeft.getFluidAmount() / this.cachedAlloyRecipe.getInputs().get(0).getQuantity() :
                    this.inputTankRight.getFluidAmount() / this.cachedAlloyRecipe.getInputs().get(0).getQuantity();

            alloyAmount = Math.min(alloyAmount,
                    this.cachedAlloyRecipe.getInputs().get(1).match(this.inputTankLeft.getFluid()) ?
                            this.inputTankLeft.getFluidAmount() /
                                    this.cachedAlloyRecipe.getInputs().get(1).getQuantity() :
                            this.inputTankRight.getFluidAmount() /
                                    this.cachedAlloyRecipe.getInputs().get(1).getQuantity());
            alloyAmount = Math.min(alloyAmount, this.outputTank.fill(
                    new FluidStack(this.cachedAlloyRecipe.getOutput(), this.outputTank.getCapacity()), false)
                    / this.cachedAlloyRecipe.getOutput().amount);

            if (alloyAmount > 0)
            {
                if (this.cachedAlloyRecipe.getInputs().get(0).match(this.inputTankLeft.getFluid()))
                {
                    this.inputTankLeft.drain(this.cachedAlloyRecipe.getInputs().get(0).getQuantity() * alloyAmount,
                            true);
                    this.inputTankRight.drain(this.cachedAlloyRecipe.getInputs().get(1).getQuantity() * alloyAmount,
                            true);
                }
                else
                {
                    this.inputTankLeft.drain(this.cachedAlloyRecipe.getInputs().get(1).getQuantity() * alloyAmount,
                            true);
                    this.inputTankRight.drain(this.cachedAlloyRecipe.getInputs().get(0).getQuantity() * alloyAmount,
                            true);
                }
                this.outputTank.fill(new FluidStack(this.cachedAlloyRecipe.getOutput(),
                        alloyAmount * this.cachedAlloyRecipe.getOutput().amount), true);
            }
            this.cachedAlloyRecipe = null;
        }
    }

    private void startCasting(InventoryHandler inventory, MaterialShape shape)
    {
        if (this.outputTank.getFluidAmount() > 0 && inventory.getStackInSlot(2).isEmpty())
        {
            Materials.getMetalFromFluid(this.outputTank.getFluid()).ifPresent(metal ->
            {
                int toDrain = 0;
                ItemStack toFill = ItemStack.EMPTY;
                if ((shape == MaterialShape.PLATE || shape == MaterialShape.INGOT) &&
                        this.outputTank.getFluidAmount() >= 144)
                {
                    toFill = shape == MaterialShape.PLATE ?
                            Materials.getPlateFromMetal(metal).copy() :
                            Materials.getIngotFromMetal(metal).copy();
                    toDrain = 144;
                    this.castSpeed = 1 / (144 / 6F);
                }
                else if (shape == MaterialShape.BLOCK && this.outputTank.getFluidAmount() >= 1296)
                {
                    toFill = Materials.getBlockFromMetal(metal).copy();
                    toDrain = 1496;
                    this.castSpeed = 1 / (1296 / 6F);
                }
                if (inventory.getStackInSlot(3).isEmpty() || ItemUtils.deepEquals(inventory.getStackInSlot(3), toFill))
                {
                    this.outputTank.drain(toDrain, true);
                    inventory.setStackInSlot(2, toFill);
                }
            });
        }
    }

    private void castLogic(InventoryHandler inventory)
    {
        if (inventory.getStackInSlot(2).isEmpty())
            return;
        if (this.castProgress >= 1)
        {
            if (inventory.getStackInSlot(3).isEmpty())
                inventory.setStackInSlot(3, inventory.extractItem(2, 1, false));
            else
            {
                inventory.extractItem(2, 1, false);
                inventory.getStackInSlot(3).grow(1);
            }
            this.castProgress = 0;
        }
        else
            this.castProgress += this.castSpeed;
    }

    private boolean acceptMelt(MeltRecipe recipe)
    {
        if (this.inputTankLeft.getFluidAmount() == 0 && this.inputTankRight.getFluidAmount() == 0)
            return true;
        if (recipe.getOutput().isFluidEqual(this.inputTankLeft.getFluid()) ||
                recipe.getOutput().isFluidEqual(this.inputTankRight.getFluid()))
            return true;
        if (this.inputTankLeft.getFluidAmount() != 0 && this.inputTankRight.getFluidAmount() != 0)
            return false;

        List<RecipeBase> recipes = Collections.emptyList();
        if (this.inputTankLeft.getFluidAmount() > 0)
            recipes = RecipeHandler.getRecipesLike(RecipeHandler.ALLOY_UID, recipe.getOutput(),
                    this.inputTankLeft.getFluid());
        else if (this.inputTankRight.getFluidAmount() > 0)
            recipes = RecipeHandler.getRecipesLike(RecipeHandler.ALLOY_UID, recipe.getOutput(),
                    this.inputTankRight.getFluid());

        if (!recipes.isEmpty())
        {
            if (this.outputTank.getFluidAmount() != 0 && recipes.stream().anyMatch(alloyRecipe -> alloyRecipe
                    .getRecipeOutputs(FluidStack.class).get(0).match(this.outputTank.getFluid())))
                return true;
            else return this.outputTank.getFluidAmount() == 0;
        }
        return false;
    }

    private boolean fillTanks(FluidStack fluid, boolean doFill)
    {
        if (fluid.isFluidEqual(this.inputTankLeft.getFluid()))
            return this.inputTankLeft.fill(fluid, doFill) == fluid.amount;
        else if (fluid.isFluidEqual(this.inputTankRight.getFluid()))
            return this.inputTankRight.fill(fluid, doFill) == fluid.amount;
        else if (this.inputTankLeft.getFluidAmount() == 0)
            return this.inputTankLeft.fill(fluid, doFill) == fluid.amount;
        else
            return this.inputTankRight.fill(fluid, doFill) == fluid.amount;
    }

    private int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getTemperature(this.pos) * 20);
    }

    public int getBurnTimeScaled(final int scale)
    {
        if (this.currentBurnTime != 0 && this.maxBurnTime != 0)
            return (int) (this.currentBurnTime * scale / this.maxBurnTime);
        return 0;
    }

    public int getHeatScaled(final int scale)
    {
        if (this.heat != 0 && this.maxHeat != 0)
            return (int) (this.heat * scale / this.maxHeat);
        return 0;
    }

    public int getMeltProgressScaled(final int scale)
    {
        return (int) (scale * this.meltProgress);
    }

    public int getCastProgressScaled(final int scale)
    {
        return (int) (scale * this.castProgress);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("heat", this.heat);
        tag.setFloat("currentBurnTime", this.currentBurnTime);
        tag.setFloat("maxBurnTime", this.maxBurnTime);
        tag.setFloat("meltProgress", this.meltProgress);
        tag.setFloat("castProgress", this.castProgress);
        tag.setFloat("castSpeed", this.castSpeed);

        tag.setTag("inputTankLeft", this.inputTankLeft.writeToNBT(new NBTTagCompound()));
        tag.setTag("inputTankRight", this.inputTankRight.writeToNBT(new NBTTagCompound()));
        tag.setTag("outputTank", this.outputTank.writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.heat = tag.getFloat("heat");
        this.currentBurnTime = tag.getFloat("currentBurnTime");
        this.maxBurnTime = tag.getFloat("maxBurnTime");
        this.meltProgress = tag.getFloat("meltProgress");
        this.castProgress = tag.getFloat("castProgress");
        this.castSpeed = tag.getFloat("castSpeed");

        this.inputTankLeft.readFromNBT(tag.getCompoundTag("inputTankLeft"));
        this.inputTankRight.readFromNBT(tag.getCompoundTag("inputTankRight"));
        this.outputTank.readFromNBT(tag.getCompoundTag("outputTank"));
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("alloycauldron", player).player(player).inventory(21, 113).hotbar(21, 171)
                .addInventory().tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .recipeSlot(0, RecipeHandler.MELTING_UID, 0, 18, 37)
                .displaySlot(1, -1000, 0)
                .displaySlot(2, -1000, 0)
                .outputSlot(3, 175, 53)
                .fuelSlot(4, 18, 76)
                .syncFloatValue(this::getHeat, this::setHeat)
                .syncFloatValue(this::getCurrentBurnTime, this::setCurrentBurnTime)
                .syncFloatValue(this::getMaxBurnTime, this::setMaxBurnTime)
                .syncFloatValue(this::getMeltProgress, this::setMeltProgress)
                .syncFloatValue(this::getCastProgress, this::setCastProgress)
                .syncFluidValue(this::getInputFluidLeft, this::setInputFluidLeft)
                .syncFluidValue(this::getInputFluidRight, this::setInputFluidRight)
                .syncFluidValue(this::getOutputFluid, this::setOutputFluid)
                .addInventory().create();
    }

    private FluidStack getInputFluidLeft()
    {
        return this.inputTankLeft.getFluid();
    }

    private void setInputFluidLeft(FluidStack fluid)
    {
        this.inputTankLeft.setFluid(fluid);
    }

    private FluidStack getInputFluidRight()
    {
        return this.inputTankRight.getFluid();
    }

    private void setInputFluidRight(FluidStack fluid)
    {
        this.inputTankRight.setFluid(fluid);
    }

    private FluidStack getOutputFluid()
    {
        return this.outputTank.getFluid();
    }

    private void setOutputFluid(FluidStack fluid)
    {
        this.outputTank.setFluid(fluid);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.ALLOYCAULDRON.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");
        switch (actionID)
        {
            case "LEFT_TANK_VOID":
                if (this.inputTankLeft.getFluidAmount() != 0 && (this.outputTank.getFluidAmount() == 0 ||
                        this.inputTankLeft.getFluid().isFluidEqual(this.outputTank.getFluid())))
                {
                    int toFill = this.outputTank.fill(this.inputTankLeft.drain(this.inputTankLeft.getFluidAmount(),
                            false), false);

                    this.outputTank.fill(this.inputTankLeft.drain(toFill, true), true);
                }
                break;
            case "RIGHT_TANK_VOID":
                if (this.inputTankRight.getFluidAmount() != 0 && (this.outputTank.getFluidAmount() == 0 ||
                        this.inputTankRight.getFluid().isFluidEqual(this.outputTank.getFluid())))
                {
                    int toFill = this.outputTank.fill(this.inputTankRight.drain(this.inputTankRight.getFluidAmount(),
                            false), false);

                    this.outputTank.fill(this.inputTankRight.drain(toFill, true), true);
                }
                break;
            case "OUTPUT_TANK_VOID":
                this.outputTank.setFluid(null);
                break;
            case "ALLOY":
                this.alloyLogic();
                break;
            case "CAST_INGOT":
                this.startCasting(inventory, MaterialShape.INGOT);
                break;
            case "CAST_PLATE":
                this.startCasting(inventory, MaterialShape.PLATE);
                break;
            case "CAST_BLOCK":
                this.startCasting(inventory, MaterialShape.BLOCK);
                break;
        }
    }
}
