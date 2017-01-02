package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.tile.TileTank;
import net.qbar.common.util.FluidUtils;

public class BlockTank extends BlockMachineBase
{
    public BlockTank()
    {
        super("tank", Material.IRON);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;
        if (!w.isRemote)
        {
            final TileTank tank = (TileTank) w.getTileEntity(pos);

            if (tank != null)
            {
                if (FluidUtils.drainPlayerHand(tank.getTank(), player)
                        || FluidUtils.fillPlayerHand(tank.getTank(), player))
                    return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileTank();
    }
}
