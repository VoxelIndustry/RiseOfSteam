package net.qbar.common.multiblock;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.IWrenchable;
import net.qbar.common.block.BlockMachineBase;

public abstract class BlockMultiblockBase extends BlockMachineBase implements IWrenchable
{
    public static final PropertyBool      MULTIBLOCK_GAG = PropertyBool.create("multiblockgag");
    public static final PropertyDirection FACING         = BlockHorizontal.FACING;

    private final IMultiblockDescriptor   descriptor;

    private final AxisAlignedBB           XCACHED_AABB;
    private final AxisAlignedBB           ZCACHED_AABB;

    public BlockMultiblockBase(final String name, final Material material, final IMultiblockDescriptor descriptor)
    {
        super(name, material);

        this.descriptor = descriptor;

        this.XCACHED_AABB = new AxisAlignedBB(-descriptor.getOffsetZ(), -descriptor.getOffsetY(),
                -descriptor.getOffsetX(), descriptor.getLength() - descriptor.getOffsetZ(),
                descriptor.getHeight() - descriptor.getOffsetY(), descriptor.getWidth() - descriptor.getOffsetX());
        this.ZCACHED_AABB = new AxisAlignedBB(-descriptor.getOffsetX(), -descriptor.getOffsetY(),
                -descriptor.getOffsetZ(), descriptor.getWidth() - descriptor.getOffsetX(),
                descriptor.getHeight() - descriptor.getOffsetY(), descriptor.getLength() - descriptor.getOffsetZ());

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false)
                .withProperty(BlockMultiblockBase.FACING, EnumFacing.NORTH));
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

    public IBlockState getGhostState(final IBlockState state, final VisibilityModelState visibilityState)
    {
        return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, visibilityState);
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return EnumBlockRenderType.INVISIBLE;
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    public static EnumFacing getFacing(final int meta)
    {
        final int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockMultiblockBase.FACING, BlockMultiblockBase.getFacing(meta))
                .withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, Boolean.valueOf((meta & 8) > 0));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(BlockMultiblockBase.FACING).getIndex();

        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG).booleanValue())
            i |= 8;
        return i;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this,
                new IProperty[] { BlockMultiblockBase.MULTIBLOCK_GAG, BlockMultiblockBase.FACING },
                new IUnlistedProperty[] { Properties.AnimationProperty });
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World w, final BlockPos pos)
    {
        if (!state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return this.getFacing(state).getAxis() == Axis.Z ? this.ZCACHED_AABB.offset(pos)
                    : this.XCACHED_AABB.offset(pos);
        if (w.getTileEntity(pos) instanceof ITileMultiblock)
        {
            final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
            if (tile != null && !tile.isCore())
                return w.getBlockState(tile.getCorePos()).getSelectedBoundingBox(w, tile.getCorePos());
        }
        return Block.FULL_BLOCK_AABB.offset(pos);
    }

    public boolean canPlaceBlockAt(final World w, final BlockPos pos, final EnumFacing facing)
    {
        Iterable<BlockPos> searchables;
        if (facing.getAxis().equals(Axis.Z))
        {
            searchables = BlockPos.getAllInBox(
                    pos.subtract(new Vec3i(this.descriptor.getOffsetX(), this.descriptor.getOffsetY(),
                            this.descriptor.getOffsetZ())),
                    pos.add(this.descriptor.getWidth() - 1, this.descriptor.getHeight() - 1,
                            this.descriptor.getLength() - 1));
        }
        else
        {
            searchables = BlockPos.getAllInBox(
                    pos.subtract(new Vec3i(this.descriptor.getOffsetZ(), this.descriptor.getOffsetY(),
                            this.descriptor.getOffsetX())),
                    pos.add(this.descriptor.getLength() - 1, this.descriptor.getHeight() - 1,
                            this.descriptor.getWidth() - 1));
        }

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
        BlockPos corePos = pos;
        if (this.descriptor.getOffsetX() != 0 || this.descriptor.getOffsetY() != 0 || this.descriptor.getOffsetZ() != 0)
        {
            if (this.getFacing(state).getAxis().equals(Axis.Z))
                corePos = pos.add(this.descriptor.getOffsetX(), this.descriptor.getOffsetY(),
                        this.descriptor.getOffsetZ());
            else
                corePos = pos.add(this.descriptor.getOffsetZ(), this.descriptor.getOffsetY(),
                        this.descriptor.getOffsetX());

            w.setBlockState(corePos, w.getBlockState(pos));
            w.setBlockToAir(pos);
        }

        Iterable<BlockPos> searchables = null;
        if (this.getFacing(state).getAxis().equals(Axis.Z))
        {
            searchables = BlockPos.getAllInBox(
                    corePos.subtract(new Vec3i(this.descriptor.getOffsetX(), this.descriptor.getOffsetY(),
                            this.descriptor.getOffsetZ())),
                    corePos.add(this.descriptor.getWidth() - 1 - this.descriptor.getOffsetX(),
                            this.descriptor.getHeight() - 1 - this.descriptor.getOffsetY(),
                            this.descriptor.getLength() - 1 - this.descriptor.getOffsetZ()));
        }
        else
        {
            searchables = BlockPos.getAllInBox(
                    corePos.subtract(new Vec3i(this.descriptor.getOffsetZ(), this.descriptor.getOffsetY(),
                            this.descriptor.getOffsetX())),
                    corePos.add(this.descriptor.getLength() - 1 - this.descriptor.getOffsetZ(),
                            this.descriptor.getHeight() - 1 - this.descriptor.getOffsetY(),
                            this.descriptor.getWidth() - 1 - this.descriptor.getOffsetX()));
        }

        for (final BlockPos current : searchables)
        {
            if (!current.equals(corePos))
            {
                w.setBlockState(current, this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, true));
                final TileMultiblockGag gag = (TileMultiblockGag) w.getTileEntity(current);
                if (gag != null)
                    gag.setCorePos(corePos);
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
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
            final float hitZ)
    {
        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);

        if (tile != null)
        {
            if (tile.isCore())
                return ((ITileMultiblockCore) tile).onRightClick(player, facing, hitX, hitY, hitZ);
            else if (tile.isCorePresent())
                return tile.getCore().onRightClick(player, facing, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot)
    {
        return state.withProperty(BlockMultiblockBase.FACING, rot.rotate(state.getValue(BlockMultiblockBase.FACING)));
    }

    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockMultiblockBase.FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false)
                .withProperty(BlockMultiblockBase.FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
            final EnumFacing facing, final IBlockState state)
    {
        return false;
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
