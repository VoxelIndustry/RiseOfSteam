package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.common.IWrenchable;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.network.WrenchPacket;
import net.qbar.common.tile.TileStructure;

public class BlockStructure extends BlockMachineBase<TileStructure> implements IWrenchable
{
    public static final PropertyBool MULTIBLOCK_GAG = PropertyBool.create("multiblockgag");

    public BlockStructure()
    {
        super("structure", Material.IRON, TileStructure.class);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStructure.MULTIBLOCK_GAG, false));
    }

    @Override
    public boolean isOpaqueCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(final IBlockState state)
    {
        return false;
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockStructure.MULTIBLOCK_GAG) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockStructure.MULTIBLOCK_GAG, meta == 1);
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockStructure.MULTIBLOCK_GAG);
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
        if (tile != null)
            tile.breakCore();
        super.breakBlock(w, pos, state);
    }

    @Override
    public void onNeighborChange(final IBlockAccess w, final BlockPos pos, final BlockPos neighbor)
    {
        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
        if (tile != null && !tile.isCore()
                && (!tile.isCorePresent() || !(w.getTileEntity(tile.getCorePos()) instanceof TileStructure)))
            w.getTileEntity(pos).getWorld().destroyBlock(pos, false);
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
                            final EnumFacing facing, final IBlockState state, ItemStack wrench)
    {
        final ITileMultiblock tile = (ITileMultiblock) world.getTileEntity(pos);
        if (tile != null)
        {
            ((TileStructure) tile.getCore()).stepBuilding(player);
            new WrenchPacket(pos, facing).sendToAllAround(world, pos, 10);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(final World w, final int meta)
    {
        final IBlockState state = this.getStateFromMeta(meta);
        if (state.getValue(BlockStructure.MULTIBLOCK_GAG))
            return new TileMultiblockGag();
        return new TileStructure();
    }
}
