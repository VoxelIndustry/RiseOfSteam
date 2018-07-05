package net.ros.common.compat.top;

import lombok.AllArgsConstructor;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.ros.common.tile.ITileInfoList;

@AllArgsConstructor
public class TileInfoListImpl implements ITileInfoList
{
    private IProbeInfo probeInfo;

    @Override
    public void addText(String text)
    {
        probeInfo.text(text);
    }

    @Override
    public void addProgress(int current, int max)
    {
        probeInfo.progress(current, max);
    }

    @Override
    public void addItem(ItemStack stack)
    {
        probeInfo.item(stack);
    }

    @Override
    public void addFluid(FluidStack stack, int capacity)
    {
        if (stack != null)
            probeInfo.element(new FluidElement(stack, stack.amount, capacity));
        else
            probeInfo.element(new FluidElement(null, 0, capacity));
    }

    @Override
    public void addEntity(Entity e)
    {
        probeInfo.entity(e);
    }
}
