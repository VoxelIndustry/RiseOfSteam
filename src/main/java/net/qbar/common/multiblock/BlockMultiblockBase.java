package net.qbar.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
            return this.CACHED_AABB;
        if (w.getTileEntity(pos) instanceof ITileMultiblock)
        {
            final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
            return w.getBlockState(tile.getCorePos()).getBlock().getSelectedBoundingBox(state, w, tile.getCorePos());
        }
        return Block.FULL_BLOCK_AABB;
    }

    public IMultiblockDescriptor getDescriptor()
    {
        return this.descriptor;
    }
}
