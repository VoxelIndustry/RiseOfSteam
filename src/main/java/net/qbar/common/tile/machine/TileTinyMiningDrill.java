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
import net.qbar.common.item.ItemDrillCoreSample;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.ore.QBarMineral;
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

    private BlockPos                lastPos;
    private Map<QBarMineral, Float> results;

    public TileTinyMiningDrill()
    {
        super("tinyminingdrill", 2);

        this.results = new HashMap<>();
        this.lastPos = this.getPos();
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (this.getProgress() < 1 && this.getSteam() >= 20)
        {
            BlockPos toCheck = this.lastPos;

            for (int i = 0; i < 25; i++)
            {
                if (lastPos.equals(BlockPos.ORIGIN))
                    toCheck = new BlockPos(this.getPos().getX() - 7, 0, this.getPos().getZ() - 7);
                else
                {
                    if (toCheck.getY() == this.getPos().getY() - 1)
                    {
                        if (toCheck.getX() == this.getPos().getX() + 7)
                        {
                            if (toCheck.getZ() == this.getPos().getZ() + 7)
                            {
                                this.progress = 1;
                                this.setInventorySlotContents(0, ItemDrillCoreSample.getSample(results));
                            }
                            toCheck = new BlockPos(this.getPos().getX() - 7, 0, toCheck.getZ() + 1);
                        }
                        else
                            toCheck = new BlockPos(toCheck.getX() + 1, 0, toCheck.getZ());
                    }
                    else
                        toCheck = toCheck.up();
                }
                this.progress = (((toCheck.getZ() - this.getPos().getZ() + 7) * 15 * (this.getPos().getY() - 1))
                        + ((toCheck.getX() - this.getPos().getX() + 7) * (this.getPos().getY() - 1)) + toCheck.getY())
                        / (float) (15 * 15 * (this.getPos().getY() - 1));

                IBlockState state = this.world.getBlockState(toCheck);

                if (state.getBlock() instanceof BlockVeinOre)
                {
                    for (Map.Entry<QBarMineral, Float> mineral : QBarOres.getOreFromState(state)
                            .orElse(QBarOres.CASSITERITE).getMinerals().entrySet())
                    {
                        this.results.putIfAbsent(mineral.getKey(), 0F);
                        this.results.put(mineral.getKey(), mineral.getValue() *
                                (state.getValue(BlockVeinOre.RICHNESS).ordinal() + 1) + this.results.get(mineral.getKey()));
                    }
                }
                lastPos = toCheck;
            }
            //TODO: Change to real value when the portable storage is implemented
            this.drainSteam(0, true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("progress", this.progress);
        tag.setLong("lastPos", this.lastPos.toLong());

        int i = 0;
        for (Map.Entry<QBarMineral, Float> ore : this.results.entrySet())
        {
            tag.setString("oreType" + i, ore.getKey().getName());
            tag.setFloat("oreCount" + i, ore.getValue());
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
            this.results.put(QBarOres.getMineralFromName(tag.getString("oreType" + i)).orElse(null),
                    tag.getFloat("oreCount" + i));

        super.readFromNBT(tag);
    }

    public int getSteam()
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH).getSteam();
        //TODO: Change to real value when the portable storage is implemented
        return 1000;
    }

    public int drainSteam(int quantity, boolean doDrain)
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH)
                    .drainSteam(quantity, doDrain);
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
