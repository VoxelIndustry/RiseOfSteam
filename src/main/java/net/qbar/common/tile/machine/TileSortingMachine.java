package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.QBar;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.recipe.ingredient.RecipeIngredient;
import net.qbar.common.tile.TileCraftingMachineBase;
import net.qbar.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.Optional;

public class TileSortingMachine extends TileCraftingMachineBase
{

    public TileSortingMachine()
    {
        super(QBarMachines.SORTING_MACHINE);
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (this.getCurrentRecipe() == null && (!this.isInputEmpty() || !this.isBufferEmpty()))
        {
            if (this.getSteamTank().getSteam() >= this.getDescriptor().getSteamConsumption())
            {
                if (this.isBufferEmpty())
                {
                    final Object[] ingredients = new Object[this.getDescriptor().getBuffers().length];

                    for (int i = 0; i < this.getDescriptor().getInputs().length; i++)
                        ingredients[i] = this.getStackInSlot(this.getDescriptor().getInputs()[i]);

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.getDescriptor().getRecipeCategory(),
                            ingredients);
                    if (recipe.isPresent())
                    {
                        this.setCurrentRecipe(recipe.get());

                        this.setMaxProgress((int) (this.getCurrentRecipe().getTime() / this.getCraftingSpeed()));
                        int i = 0;
                        for (final RecipeIngredient<ItemStack> stack : this.getCurrentRecipe()
                                .getRecipeInputs(ItemStack.class))
                        {
                            this.decrStackSize(this.getDescriptor().getInputs()[i], stack.getRawIngredient().getCount());
                            this.setInventorySlotContents(this.getDescriptor().getBuffers()[i],
                                    stack.getRawIngredient().copy());
                            i++;
                        }
                        this.sync();
                    }
                }
                else
                {
                    final Object[] ingredients = new Object[this.getDescriptor().getBuffers().length];

                    for (int i = 0; i < this.getDescriptor().getBuffers().length; i++)
                        ingredients[i] = this.getStackInSlot(this.getDescriptor().getBuffers()[i]);

                    final Optional<QBarRecipe> recipe = QBarRecipeHandler.getRecipe(this.getDescriptor().getRecipeCategory(),
                            ingredients);
                    if (recipe.isPresent())
                    {
                        this.setCurrentRecipe(recipe.get());
                        this.setMaxProgress((int) (this.getCurrentRecipe().getTime() / this.getCraftingSpeed()));

                        this.sync();
                    }
                }
            }
        }
        if (this.getCurrentRecipe() != null && !this.isBufferEmpty())
        {
            if (this.getCurrentProgress() < this.getMaxProgress())
            {
                if (this.getSteamTank().getSteam() >= this.getDescriptor().getSteamConsumption())
                {
                    this.setCurrentProgress(this.getCurrentProgress() + this.getCurrentCraftingSpeed());
                    this.useSteam(this.getDescriptor().getSteamConsumption());
                    this.sync();
                }
            }
            else
            {
                int i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.getCurrentRecipe().getRecipeOutputs(ItemStack.class))
                {
                    if (!ItemUtils.canMergeStacks(stack.getRawIngredient(),
                            this.getStackInSlot(this.getDescriptor().getOutputs()[i])))
                        return;
                    i++;
                }

                for (final int buffer : this.getDescriptor().getBuffers())
                    this.setInventorySlotContents(buffer, ItemStack.EMPTY);
                for (int j = 0; j < this.getDescriptor().getBufferTanks().length; j++)
                    this.setBufferFluidStack(j, null);

                i = 0;
                for (final RecipeIngredient<ItemStack> stack : this.getCurrentRecipe().getRecipeOutputs(ItemStack.class))
                {
                    if (!this.getStackInSlot(this.getDescriptor().getOutputs()[i]).isEmpty())
                        this.getStackInSlot(this.getDescriptor().getOutputs()[i]).grow(stack.getRawIngredient().getCount());
                    else
                        this.setInventorySlotContents(this.getDescriptor().getOutputs()[i], stack.getRawIngredient().copy());
                    i++;
                }
                this.setCurrentRecipe(null);
                this.setCurrentProgress(0);
                this.sync();
            }
        }
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("sortingmachine", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.ORE_WASHER_UID, 0, 47, 36,
                        slot -> this.isBufferEmpty() && this.isOutputEmpty())
                .outputSlot(1, 107, 35).displaySlot(2, -1000, 0)
                .syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam).addInventory().create();
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
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBar.instance, EGui.SORTINGMACHINE.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
