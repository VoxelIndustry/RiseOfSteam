package net.ros.common.grid.node;

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
}
