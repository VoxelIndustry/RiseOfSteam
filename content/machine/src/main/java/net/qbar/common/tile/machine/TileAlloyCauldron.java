package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.recipe.type.MeltRecipe;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileAlloyCauldron extends TileMultiblockInventoryBase implements ITickable
{
    private final List<FluidTank> tanks;

    private final float maxHeat;
    private       float heat;
    private       float remainingHeat;

    private MeltRecipe currentRecipe;
    private float      remainingMeltTime;

    public TileAlloyCauldron()
    {
        super("alloycauldron", 5);
        this.tanks = new ArrayList<>(2);
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

        if (!this.getStackInSlot(4).isEmpty() && this.getStackInSlot(5).isEmpty())
        {
            this.setInventorySlotContents(5, this.decrStackSize(4, 1));
            this.remainingHeat = TileEntityFurnace.getItemBurnTime(this.getStackInSlot(5));
        }

        this.remainingHeat--;
        this.heat++;

        if (this.remainingHeat <= 0)
            this.setInventorySlotContents(5, ItemStack.EMPTY);
    }

    private void meltLogic()
    {
        if (this.heat == this.getMinimumTemp() || this.isEmpty())
            return;

        if (!this.getStackInSlot(0).isEmpty() && this.getStackInSlot(1).isEmpty())
        {
            this.setInventorySlotContents(1, this.getStackInSlot(0));
            this.setInventorySlotContents(0, ItemStack.EMPTY);
        }

        // TODO : Melt recipes
    }

    private void alloyLogic()
    {
        if (this.tanks.size() < 2)
            return;
    }

    public int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getTemperature(this.pos) * 200);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return false;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return null;
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
}
