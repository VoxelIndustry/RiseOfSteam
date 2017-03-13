package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.QBar;
import net.qbar.common.gui.EGui;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileKeypunch;

public class BlockKeypunch extends BlockMultiblockBase
{
    public BlockKeypunch()
    {
        super("keypunch", Material.IRON, Multiblocks.KEYPUNCH);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
            final float hitZ)
    {
        if (player.isSneaking())
            return false;

        player.openGui(QBar.instance, EGui.KEYPUNCH.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileKeypunch();
    }
}
