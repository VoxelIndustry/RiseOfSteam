package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.recipe.Materials;
import net.ros.common.tile.TileFluidPump;
import net.ros.common.tile.TilePipeBase;

public class BlockFluidPump extends BlockOrientableMachine<TileFluidPump>
{
    protected static final AxisAlignedBB AABB_EAST     = new AxisAlignedBB(0.00D, 0.30D, 0.00D, 1.00D, 0.80D, 0.85D);
    protected static final AxisAlignedBB AABB_NOTH     = new AxisAlignedBB(0.00D, 0.30D, 0.00D, 0.85D, 0.80D, 1.00D);
    protected static final AxisAlignedBB AABB_WEST     = new AxisAlignedBB(0.00D, 0.30D, 0.15D, 1.00D, 0.80D, 1.00D);
    protected static final AxisAlignedBB AABB_SOUTH    = new AxisAlignedBB(0.15D, 0.30D, 0.00D, 1.00D, 0.80D, 1.00D);
    protected static final AxisAlignedBB AABB_VERTICAL = new AxisAlignedBB(0.30D, 0.00D, 0.15D, 0.80D, 1.00D, 1.00D);

    public BlockFluidPump()
    {
        super("fluidpump", Material.IRON, true, true, TileFluidPump.class);
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
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block,
                                BlockPos posNeighbor)
    {
        if (!w.isRemote)
        {
            BlockPos offset = pos.subtract(posNeighbor);
            EnumFacing facing = EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ())
                    .getOpposite();

            TileFluidPump pipe = this.getWorldTile(w, pos);
            pipe.scanHandler(facing);
            pipe.scanValve(facing);
        }
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        ((TilePipeBase<?, ?>) w.getTileEntity(pos)).disconnectItself();

        super.breakBlock(w, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileFluidPump(new PipeType(PipeNature.FLUID, PipeSize.SMALL, Materials.IRON), 64);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (getFacing(state))
        {
            case EAST:
                return AABB_EAST;
            case WEST:
                return AABB_WEST;
            case NORTH:
                return AABB_NOTH;
            case SOUTH:
                return AABB_SOUTH;
            case UP:
            case DOWN:
                return AABB_VERTICAL;
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
    }
}
