package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;
import net.qbar.common.fluid.FilteredFluidTank;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileSmallMiningDrill extends TileInventoryBase implements ITileMultiblockCore, ITickable
{
    @Getter
    private SteamTank steamTank;
    @Getter
    private FluidTank fluidTank;

    @Getter
    @Setter
    private float heat, maxHeat;

    @Getter
    @Setter
    private boolean  completed;
    private BlockPos lastPos;

    public TileSmallMiningDrill()
    {
        super("smallminingdrill", 1);

        this.fluidTank = new FilteredFluidTank(32000, fluid -> fluid != null && fluid.getFluid() == FluidRegistry.WATER);
        this.steamTank = new SteamTank(0, QBarMachines.SMALL_MINING_DRILL.getSteamCapacity(),
                QBarMachines.SMALL_MINING_DRILL.getMaxPressureCapacity());

        this.heat = 0;
        this.maxHeat = 3000;
        this.lastPos = this.getPos();
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        boolean isDirty = false;

        if (!this.isCompleted()/* && this.getSteamTank().getSteam() >= QBarMachines.SMALL_MINING_DRILL.getSteamConsumption()*/)
        {
            BlockPos toCheck = this.lastPos;

            if (lastPos.equals(BlockPos.ORIGIN))
                toCheck = new BlockPos(this.getPos().getX() - 2, this.getPos().getY() - 1, this.getPos().getZ() - 2);
            else
            {

                if (toCheck.getX() == this.getPos().getX() + 2)
                {
                    if (toCheck.getZ() == this.getPos().getZ() + 2)
                    {
                        if (toCheck.getY() == 0)
                        {
                            this.setCompleted(true);
                        }
                        else
                            toCheck = new BlockPos(this.getPos().getX() - 2, toCheck.getY() - 1, this.getPos().getZ() - 2);
                    }
                    else
                        toCheck = new BlockPos(this.getPos().getX() - 2, toCheck.getY(), toCheck.getZ() + 1);
                }
                else
                    toCheck = new BlockPos(toCheck.getX() + 1, toCheck.getY(), toCheck.getZ());
            }

            IBlockState state = this.world.getBlockState(toCheck);

            if (!this.world.isAirBlock(toCheck) && state.getBlockHardness(world, toCheck) >= 0)
            {
                if (Math.abs(toCheck.getX() - this.getPos().getX()) < 2 && Math.abs(toCheck.getZ() - this.getPos().getZ()) < 2)
                    this.world.destroyBlock(toCheck, false);
                this.heat += 30;
            }

            lastPos = toCheck;
            this.steamTank.drainSteam(QBarMachines.SMALL_MINING_DRILL.getSteamConsumption(), true);
            isDirty = true;
        }

        if (this.world.getTotalWorldTime() % 5 == 0)
        {
            if (this.heat > this.getMinimumTemp())
            {
                this.heat--;
                isDirty = true;
            }
            else if (this.heat < this.getMinimumTemp())
            {
                this.heat = this.getMinimumTemp();
                isDirty = true;
            }
        }

        if (isDirty)
            this.sync();
    }

    private int getMinimumTemp()
    {
        return (int) (this.world.getBiome(this.getPos()).getFloatTemperature(this.pos) * 200);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setTag("steamTank", this.steamTank.writeToNBT(new NBTTagCompound()));
        tag.setTag("fluidTank", this.fluidTank.writeToNBT(new NBTTagCompound()));

        tag.setFloat("heat", this.heat);
        tag.setFloat("maxHeat", this.maxHeat);

        tag.setBoolean("completed", this.isCompleted());
        return tag;
    }

    private ArrayList<MultiblockSide> tmpConnections = new ArrayList<>();

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));
        if (tag.hasKey("fluidTank"))
            this.fluidTank.readFromNBT(tag.getCompoundTag("fluidTank"));

        this.heat = tag.getFloat("heat");
        this.maxHeat = tag.getFloat("maxHeat");

        this.completed = tag.getBoolean("completed");
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }
}
