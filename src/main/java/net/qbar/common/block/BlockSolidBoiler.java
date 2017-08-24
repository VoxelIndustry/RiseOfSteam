package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.tile.machine.TileSolidBoiler;

public class BlockSolidBoiler extends BlockMultiblockBase<TileSolidBoiler>
{
    public BlockSolidBoiler()
    {
        super("solid_boiler", Material.IRON, TileSolidBoiler.class);
    }

    @Override
    public void onBlockDestroyedByExplosion(final World w, final BlockPos pos, final Explosion exp)
    {
        super.onBlockDestroyedByExplosion(w, pos, exp);

        w.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2, true);
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
    public TileSolidBoiler getTile(final World w, final IBlockState state)
    {
        return new TileSolidBoiler();
    }
}
