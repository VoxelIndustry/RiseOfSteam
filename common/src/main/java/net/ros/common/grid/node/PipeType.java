package net.ros.common.grid.node;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.nbt.NBTTagCompound;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;

@Data
@AllArgsConstructor
public class PipeType
{
    private PipeNature nature;
    private PipeSize   size;
    private Metal      metal;

    public PipeType(NBTTagCompound tag)
    {
        this.fromNBT(tag);
    }

    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setString("nature", this.nature.name());
        tag.setString("size", this.size.name());
        tag.setString("metal", this.metal.getName());

        return tag;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("nature"))
            this.nature = PipeNature.valueOf(tag.getString("nature"));
        else
            this.nature = PipeNature.FLUID;

        if (tag.hasKey("size"))
            this.size = PipeSize.valueOf(tag.getString("size"));
        else
            this.size = PipeSize.SMALL;

        if (tag.hasKey("metal"))
            this.metal = Materials.metals.byName(tag.getString("metal")).get();
        else
            this.metal = Materials.IRON;
    }

    private static Table<PipeSize, Metal, Integer> pipeTransferRates;
    private static Table<PipeSize, Metal, Float>   pipePressures;

    static
    {
        pipeTransferRates = HashBasedTable.create();
        pipePressures = HashBasedTable.create();

        // Fluid pipes
        pipeTransferRates.put(PipeSize.SMALL, Materials.IRON, 64);
        pipeTransferRates.put(PipeSize.MEDIUM, Materials.IRON, 256);
        pipeTransferRates.put(PipeSize.LARGE, Materials.IRON, 1024);
        pipeTransferRates.put(PipeSize.HUGE, Materials.IRON, 4096);
        pipeTransferRates.put(PipeSize.EXTRA_HUGE, Materials.IRON, 16384);

        pipeTransferRates.put(PipeSize.SMALL, Materials.CAST_IRON, 128);
        pipeTransferRates.put(PipeSize.MEDIUM, Materials.CAST_IRON, 512);
        pipeTransferRates.put(PipeSize.LARGE, Materials.CAST_IRON, 2048);
        pipeTransferRates.put(PipeSize.HUGE, Materials.CAST_IRON, 8192);
        pipeTransferRates.put(PipeSize.EXTRA_HUGE, Materials.CAST_IRON, 32768);

        // Steam pipes
        pipeTransferRates.put(PipeSize.SMALL, Materials.BRASS, 64);
        pipeTransferRates.put(PipeSize.MEDIUM, Materials.BRASS, 256);
        pipeTransferRates.put(PipeSize.LARGE, Materials.BRASS, 1024);
        pipeTransferRates.put(PipeSize.HUGE, Materials.BRASS, 4096);
        pipeTransferRates.put(PipeSize.EXTRA_HUGE, Materials.BRASS, 16384);

        pipeTransferRates.put(PipeSize.SMALL, Materials.STEEL, 128);
        pipeTransferRates.put(PipeSize.MEDIUM, Materials.STEEL, 512);
        pipeTransferRates.put(PipeSize.LARGE, Materials.STEEL, 2048);
        pipeTransferRates.put(PipeSize.HUGE, Materials.STEEL, 8192);
        pipeTransferRates.put(PipeSize.EXTRA_HUGE, Materials.STEEL, 32768);

        pipePressures.put(PipeSize.SMALL, Materials.BRASS, 1.5f);
        pipePressures.put(PipeSize.MEDIUM, Materials.BRASS, 2f);
        pipePressures.put(PipeSize.LARGE, Materials.BRASS, 2.5f);
        pipePressures.put(PipeSize.HUGE, Materials.BRASS, 3f);
        pipePressures.put(PipeSize.EXTRA_HUGE, Materials.BRASS, 3.5f);

        pipePressures.put(PipeSize.SMALL, Materials.STEEL, 3f);
        pipePressures.put(PipeSize.MEDIUM, Materials.STEEL, 4f);
        pipePressures.put(PipeSize.LARGE, Materials.STEEL, 5f);
        pipePressures.put(PipeSize.HUGE, Materials.STEEL, 6f);
        pipePressures.put(PipeSize.EXTRA_HUGE, Materials.STEEL, 7f);
    }

    public static int getTransferRate(PipeType type)
    {
        if (type == null)
            return 0;
        return pipeTransferRates.get(type.getSize(), type.getMetal());
    }

    public static float getPressure(PipeType type)
    {
        if (type == null)
            return 0;
        return pipePressures.get(type.getSize(), type.getMetal());
    }
}
