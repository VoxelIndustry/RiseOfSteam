package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.QBar;
import net.qbar.common.gui.EGui;
import net.qbar.common.tile.TileBoiler;
import net.qbar.common.util.FluidUtils;

public class BlockBoiler extends BlockMachineBase
{
    public BlockBoiler()
    {
        super("boiler", Material.IRON);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;

        final TileBoiler boiler = (TileBoiler) w.getTileEntity(pos);

        if (boiler != null)
        {
            if (FluidUtils.drainPlayerHand(boiler.getFluidTank().getInternalFluidHandler(), player)
                    || FluidUtils.fillPlayerHand(boiler.getFluidTank().getInternalFluidHandler(), player))
                return true;
        }
        player.openGui(QBar.instance, EGui.BOILER.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileBoiler();
    }
}
