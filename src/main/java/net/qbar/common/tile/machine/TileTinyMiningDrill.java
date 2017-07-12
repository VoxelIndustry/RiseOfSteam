package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.block.BlockVeinOre;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TileTinyMiningDrill extends TileInventoryBase implements ITickable, ITileMultiblockCore
{
    @Getter
    @Setter
    private float progress;

    private BlockPos              lastPos;
    private Map<QBarOre, Integer> results;

    public TileTinyMiningDrill()
    {
        super("tinyminingdrill", 2);

        this.results = new HashMap<>();
        this.lastPos = this.getPos();
    }

    @Override
    public void update()
    {
        if (this.getProgress() < 1 && this.getSteam() >= 20)
        {
            BlockPos toCheck = BlockPos.ORIGIN;

            for (int i = 0; i < 25; i++)
            {
                if (lastPos == this.getPos())
                    toCheck = new BlockPos(this.getPos().getX() - 7, 0, this.getPos().getZ() - 7);
                else
                {
                    if (toCheck.getY() == 64)
                    {
                        if (toCheck.getX() == this.getPos().getZ() + 7)
                            toCheck = new BlockPos(this.getPos().getX() - 7, 0, toCheck.getZ() + 1);
                        else
                            toCheck = new BlockPos(toCheck.getX() + 1, 0, toCheck.getZ());
                    }
                    else
                        toCheck = toCheck.up();
                }

                IBlockState state = this.world.getBlockState(toCheck);

                if (state.getBlock() instanceof BlockVeinOre)
                {
                    QBarOre ore = QBarOres.getOreFromName(
                            state.getValue(((BlockVeinOre) state.getBlock()).getVARIANTS())).orElse(QBarOres.COPPER);
                    this.results.put(ore, this.results.getOrDefault(ore, 0) + 1);
                }

                lastPos = toCheck;
            }
            this.drainSteam(20, true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("progress", this.progress);
        tag.setLong("lastPos", this.lastPos.toLong());

        int i = 0;
        for (Map.Entry<QBarOre, Integer> ore : this.results.entrySet())
        {
            tag.setString("oreType" + i, ore.getKey().getName());
            tag.setInteger("oreCount" + i, ore.getValue());
            i++;
        }
        tag.setInteger("ores", i);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.progress = tag.getFloat("progress");
        this.lastPos = BlockPos.fromLong(tag.getLong("lastPos"));

        for (int i = 0; i < tag.getInteger("ores"); i++)
            this.results.put(QBarOres.getOreFromName(tag.getString("oreType" + i)).orElse(null),
                    tag.getInteger("oreCount" + i));

        super.readFromNBT(tag);
    }

    public int getSteam()
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH).getSteam();
        return 0;
    }

    public int drainSteam(int quantity, boolean doDrain)
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH).drainSteam(quantity, doDrain);
        return 0;
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
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }
}
