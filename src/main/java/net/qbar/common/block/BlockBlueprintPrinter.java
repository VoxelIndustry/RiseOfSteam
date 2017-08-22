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

import javax.annotation.Nullable;

public class BlockBlueprintPrinter extends BlockOrientableMachine<TileBlueprintPrinter>
{
    public BlockBlueprintPrinter()
    {
        super("blueprintprinter", Material.WOOD, true, false, TileBlueprintPrinter.class);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state, final EntityPlayer player,
                                    final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
                                    final float hitZ)
    {
        if (player.isSneaking())
            return false;
        player.openGui(QBar.instance, EGui.BLUEPRINTPRINTER.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileBlueprintPrinter();
    }
}