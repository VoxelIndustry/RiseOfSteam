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
import net.qbar.common.tile.machine.TileBlueprintPrinter;
import net.qbar.common.tile.machine.TileCraftCardLibrary;

import javax.annotation.Nullable;

public class BlockCraftCardLibrary extends BlockOrientableMachine<TileCraftCardLibrary>
{
    public BlockCraftCardLibrary()
    {
        super("craftcardlibrary", Material.WOOD, true, false, TileCraftCardLibrary.class);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state, final EntityPlayer player,
                                    final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
                                    final float hitZ)
    {
        if (player.isSneaking())
            return false;
        player.openGui(QBar.instance, EGui.CRAFTCARDLIBRARY.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileCraftCardLibrary();
    }
}