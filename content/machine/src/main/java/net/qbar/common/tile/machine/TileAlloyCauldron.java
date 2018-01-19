package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.recipe.QBarMaterials;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.type.AlloyRecipe;
import net.qbar.common.recipe.type.MeltRecipe;
import net.qbar.common.tile.TileMultiblockInventoryBase;
import net.qbar.common.util.ItemUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class TileAlloyCauldron extends TileMultiblockInventoryBase implements ITickable, IActionReceiver
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
        super("alloycauldron", 5);
        this.inputTankLeft = new FluidTank(12 * Fluid.BUCKET_VOLUME);
        this.inputTankRight = new FluidTank(12 * Fluid.BUCKET_VOLUME);
        this.outputTank = new FluidTank(24 * Fluid.BUCKET_VOLUME);
        this.maxHeat = 1500;
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        this.heatLogic();
        this.meltLogic();
        this.castLogic();
    }

    private void heatLogic()
    {
        if (this.heat > this.getMinimumTemp())
            this.heat -= 0.5f;
        if (this.heat < this.getMinimumTemp())
            this.heat = this.getMinimumTemp();
        if (this.heat >= this.maxHeat)
            return;

        if (!this.getStackInSlot(4).isEmpty() && this.maxBurnTime == 0)
            this.maxBurnTime = TileEntityFurnace.getItemBurnTime(this.decrStackSize(4, 1));

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

    private void meltLogic()
    {
        if (this.heat == this.getMinimumTemp() || this.isEmpty())
            return;

        if (!this.getStackInSlot(0).isEmpty() && this.getStackInSlot(1).isEmpty() &&
                !ItemUtils.deepEquals(cachedIngredient, this.getStackInSlot(0)))
        {
            QBarRecipeHandler.getRecipe(QBarRecipeHandler.MELTING_UID, this.getStackInSlot(0))
                    .ifPresent(recipe ->
                    {
                        if (this.acceptMelt((MeltRecipe) recipe))
                            this.currentRecipe = (MeltRecipe) recipe;
                    });
            this.cachedIngredient = this.getStackInSlot(0).copy();
        }

        if (this.currentRecipe != null && this.currentRecipe.getLowMeltingPoint() <= this.heat &&
                fillTanks(this.currentRecipe.getOutput(), false))
        {
            if (this.getStackInSlot(1).isEmpty())
            {
                this.setInventorySlotContents(1, this.decrStackSize(0, 1));
                this.cachedIngredient = ItemStack.EMPTY;
            }
            float efficiency = (this.heat - this.currentRecipe.getLowMeltingPoint()) /
                    (this.currentRecipe.getHighMeltingPoint() - this.currentRecipe.getLowMeltingPoint());
            this.meltProgress += ((float) 1 / this.currentRecipe.getTime()) * efficiency;

            if (this.meltProgress > 1)
            {
                this.fillTanks(this.currentRecipe.getOutput(), true);
                this.currentRecipe = null;
                this.setInventorySlotContents(1, ItemStack.EMPTY);
                this.meltProgress = 0;
                this.sync();
            }
        }
    }

    private AlloyRecipe cachedAlloyRecipe;
    private MutablePair<FluidStack, FluidStack> cachedAlloyIngredients = new MutablePair<>();

    private void alloyLogic()
    {
        if (this.inputTankLeft.getFluidAmount() == 0 || this.inputTankRight.getFluidAmount() == 0)
            return;

        if (this.cachedAlloyRecipe == null &&
                (!this.inputTankLeft.getFluid().isFluidStackIdentical(cachedAlloyIngredients.getLeft()) ||
                        !this.inputTankRight.getFluid().isFluidStackIdentical(cachedAlloyIngredients.getRight())))
        {
            Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(QBarRecipeHandler.ALLOY_UID,
                    this.inputTankLeft.getFluid(), this.inputTankRight.getFluid());
            if (!recipe.isPresent())
                recipe = QBarRecipeHandler.getRecipe(QBarRecipeHandler.ALLOY_UID,
                        this.inputTankRight.getFluid(), this.inputTankLeft.getFluid());

            recipe.ifPresent(qBarRecipe -> cachedAlloyRecipe = (AlloyRecipe) qBarRecipe);

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

    private void startCasting(QBarMaterials.MaterialShape shape)
    {
        if (this.outputTank.getFluidAmount() > 0 && this.getStackInSlot(2).isEmpty())
        {
            QBarMaterials.getMetalFromFluid(this.outputTank.getFluid()).ifPresent(metal ->
            {
                int toDrain = 0;
                ItemStack toFill = ItemStack.EMPTY;
                if ((shape == QBarMaterials.MaterialShape.PLATE || shape == QBarMaterials.MaterialShape.INGOT) &&
                        this.outputTank.getFluidAmount() >= 144)
                {
                    toFill = shape == QBarMaterials.MaterialShape.PLATE ?
                            QBarMaterials.getPlateFromMetal(metal).copy() :
                            QBarMaterials.getIngotFromMetal(metal).copy();
                    toDrain = 144;
                    this.castSpeed = 1 / (144 / 6F);
                }
                else if (shape == QBarMaterials.MaterialShape.BLOCK && this.outputTank.getFluidAmount() >= 1296)
                {
                    toFill = QBarMaterials.getBlockFromMetal(metal).copy();
                    toDrain = 1496;
                    this.castSpeed = 1 / (1296 / 6F);
                }
                if (this.getStackInSlot(3).isEmpty() || ItemUtils.deepEquals(this.getStackInSlot(3), toFill))
                {
                    this.outputTank.drain(toDrain, true);
                    this.setInventorySlotContents(2, toFill);
                }
            });
        }
    }

    private void castLogic()
    {
        if (this.getStackInSlot(2).isEmpty())
            return;
        if (this.castProgress >= 1)
        {
            if (this.getStackInSlot(3).isEmpty())
                this.setInventorySlotContents(3, this.decrStackSize(2, 1));
            else
            {
                this.decrStackSize(2, 1);
                this.getStackInSlot(3).grow(1);
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

        List<QBarRecipe> recipes = Collections.emptyList();
        if (this.inputTankLeft.getFluidAmount() > 0)
            recipes = QBarRecipeHandler.getRecipesLike(QBarRecipeHandler.ALLOY_UID, recipe.getOutput(),
                    this.inputTankLeft.getFluid());
        else if (this.inputTankRight.getFluidAmount() > 0)
            recipes = QBarRecipeHandler.getRecipesLike(QBarRecipeHandler.ALLOY_UID, recipe.getOutput(),
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
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing side)
    {
        return false;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("alloycauldron", player).player(player.inventory).inventory(21, 113).hotbar(21, 171)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.MELTING_UID, 0, 18, 37)
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
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.ALLOYCAULDRON.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
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
                this.startCasting(QBarMaterials.MaterialShape.INGOT);
                break;
            case "CAST_PLATE":
                this.startCasting(QBarMaterials.MaterialShape.PLATE);
                break;
            case "CAST_BLOCK":
                this.startCasting(QBarMaterials.MaterialShape.BLOCK);
                break;
        }
    }
}
