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
import net.qbar.common.fluid.MultiFluidTank;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.QBarRecipeHelper;
import net.qbar.common.recipe.type.AlloyRecipe;
import net.qbar.common.recipe.type.MeltRecipe;
import net.qbar.common.tile.TileMultiblockInventoryBase;
import net.qbar.common.util.ItemUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class TileAlloyCauldron extends TileMultiblockInventoryBase implements ITickable
{
    private final MultiFluidTank tanks;

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

    public TileAlloyCauldron()
    {
        super("alloycauldron", 6);
        this.tanks = new MultiFluidTank(16 * Fluid.BUCKET_VOLUME, 3);
        this.maxHeat = 1500;
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        this.heatLogic();
        this.meltLogic();
        this.alloyLogic();
    }

    private void heatLogic()
    {
        if (this.heat > this.getMinimumTemp())
            this.heat -= 0.5f;
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
                        this.currentRecipe = (MeltRecipe) recipe;
                        this.cachedIngredient = this.getStackInSlot(0).copy();
                    });
        }

        if (this.currentRecipe != null && this.currentRecipe.getLowMeltingPoint() <= this.heat)
        {
            if (this.getStackInSlot(1).isEmpty())
            {
                this.setInventorySlotContents(1, this.decrStackSize(0, 1));
                this.cachedIngredient = ItemStack.EMPTY;
            }
            float efficiency = (this.heat - this.currentRecipe.getLowMeltingPoint()) /
                    (this.currentRecipe.getHighMeltingPoint() - this.currentRecipe.getLowMeltingPoint());
            this.meltProgress += ((float) 1 / this.currentRecipe.getTime()) * efficiency;

            if (this.meltProgress > 1 && this.fillTanks(this.currentRecipe.getOutput().copy()))
            {
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
        if (this.tanks.getFluids().size() < 2)
            return;

        if (this.cachedAlloyRecipe == null)
        {
            Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(QBarRecipeHandler.ALLOY_UID, this.tanks
                            .getFluids().get(0),
                    this.tanks.getFluids().get(1));
            if(recipe.isPresent())
            {
                cachedAlloyRecipe = (AlloyRecipe) recipe.get();
                cachedAlloyIngredients.setLeft(this.tanks.getFluids().get(0).copy());
                cachedAlloyIngredients.setRight(this.tanks.getFluids().get(1).copy());
            }
        }
    }

    private boolean fillTanks(FluidStack fluid)
    {
        if (this.tanks.fill(fluid, false) < fluid.amount)
            return false;
        this.tanks.fill(fluid, true);
        return true;
    }

    private int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getTemperature(this.pos) * 200);
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("heat", this.heat);
        tag.setFloat("currentBurnTime", this.currentBurnTime);
        tag.setFloat("maxBurnTime", this.maxBurnTime);
        tag.setFloat("meltProgress", this.meltProgress);

        tag.setTag("multiFluidTank", this.tanks.writeToNBT(new NBTTagCompound()));

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

        this.tanks.readFromNBT(tag.getCompoundTag("multiFluidTank"));
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
        return new ContainerBuilder("alloycauldron", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.MELTING_UID, 0, 18, 20)
                .displaySlot(1, -1000, 0)
                .outputSlot(2, 148, 36)
                .fuelSlot(4, 18, 59)
                .syncFloatValue(this::getHeat, this::setHeat)
                .syncFloatValue(this::getCurrentBurnTime, this::setCurrentBurnTime)
                .syncFloatValue(this::getMaxBurnTime, this::setMaxBurnTime)
                .syncFloatValue(this::getMeltProgress, this::setMeltProgress)
                .addInventory().create();
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
}
