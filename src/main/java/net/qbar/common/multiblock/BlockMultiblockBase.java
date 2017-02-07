package net.qbar.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.common.block.BlockMachineBase;

public abstract class BlockMultiblockBase extends BlockMachineBase
{
    public static final PropertyBool    MULTIBLOCK_GAG = PropertyBool.create("multiblockgag");

    private final IMultiblockDescriptor descriptor;

    private final AxisAlignedBB         CACHED_AABB;

    public BlockMultiblockBase(final String name, final Material material, final IMultiblockDescriptor descriptor)
    {
        super(name, material);

        this.descriptor = descriptor;

        this.CACHED_AABB = new AxisAlignedBB(-descriptor.getOffsetX(), -descriptor.getOffsetY(),
                -descriptor.getOffsetZ(), descriptor.getWidth(), descriptor.getHeight(), descriptor.getLength());

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false));
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return EnumBlockRenderType.INVISIBLE;
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, meta == 1);
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockMultiblockBase.MULTIBLOCK_GAG);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World w, final BlockPos pos)
    {
        if (!state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
        {
            return this.CACHED_AABB.offset(pos);
        }
        if (w.getTileEntity(pos) instanceof ITileMultiblock)
        {
            final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
            if (tile != null && !tile.isCore())
                return w.getBlockState(tile.getCorePos()).getSelectedBoundingBox(w, tile.getCorePos());
        }
        return Block.FULL_BLOCK_AABB.offset(pos);
    }

    @Override
    public boolean canPlaceBlockAt(final World w, final BlockPos pos)
    {
        final Iterable<BlockPos> searchables = BlockPos.getAllInBox(
                pos.subtract(new Vec3i(this.descriptor.getOffsetX(), this.descriptor.getOffsetY(),
                        this.descriptor.getOffsetZ())),
                pos.add(this.descriptor.getWidth() - 1, this.descriptor.getHeight() - 1,
                        this.descriptor.getLength() - 1));
        for (final BlockPos current : searchables)
        {
            if (!w.getBlockState(current).getBlock().isReplaceable(w, current))
                return false;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        final Iterable<BlockPos> searchables = BlockPos.getAllInBox(
                pos.subtract(new Vec3i(this.descriptor.getOffsetX(), this.descriptor.getOffsetY(),
                        this.descriptor.getOffsetZ())),
                pos.add(this.descriptor.getWidth() - 1, this.descriptor.getHeight() - 1,
                        this.descriptor.getLength() - 1));
        for (final BlockPos current : searchables)
        {
            if (!current.equals(pos))
            {
                w.setBlockState(current, this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, true));
                final TileMultiblockGag gag = (TileMultiblockGag) w.getTileEntity(current);
                if (gag != null)
                    gag.setCorePos(pos);
            }
        }
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
        if (tile != null && !tile.isCore() && !tile.isCorePresent())
            w.getTileEntity(pos).getWorld().destroyBlock(pos, false);
    }

    @Override
    public TileEntity createNewTileEntity(final World w, final int meta)
    {
        final IBlockState state = this.getStateFromMeta(meta);
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return new TileMultiblockGag();
        return this.getTile(w, state);
    }

    public abstract TileEntity getTile(final World w, final IBlockState state);

    public IMultiblockDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
