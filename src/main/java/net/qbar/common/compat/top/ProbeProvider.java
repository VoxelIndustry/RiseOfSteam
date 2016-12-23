package net.qbar.common.compat.top;

import java.util.ArrayList;
import java.util.List;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.QBar;
import net.qbar.common.tile.ITileInfoProvider;

public class ProbeProvider implements IProbeInfoProvider
{
    @Override
    public String getID()
    {
        return QBar.MODID;
    }

    @Override
    public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player,
            final World world, final IBlockState blockState, final IProbeHitData data)
    {
        final TileEntity tile = world.getTileEntity(data.getPos());
        if (tile instanceof ITileInfoProvider)
        {
            final List<String> lines = new ArrayList<>();
            ((ITileInfoProvider) tile).addInfo(lines);
            lines.forEach(probeInfo::text);
        }
    }
}
