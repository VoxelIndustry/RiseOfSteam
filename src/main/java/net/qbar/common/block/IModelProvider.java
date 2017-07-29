package net.qbar.common.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelProvider
{
    @SideOnly(Side.CLIENT)
    int getItemModelCount();

    @SideOnly(Side.CLIENT)
    String getItemModelFromMeta(int itemMeta);
}
