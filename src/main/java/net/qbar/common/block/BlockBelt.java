package net.qbar.common.block;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.qbar.common.grid.GridManager;
import net.qbar.common.tile.TileBelt;

public class BlockBelt extends BlockOrientableMachine
{
    protected static final AxisAlignedBB AABB_OCT_TOP_NW  = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D);
    protected static final AxisAlignedBB AABB_OCT_TOP_NE  = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    protected static final AxisAlignedBB AABB_OCT_TOP_SW  = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_OCT_TOP_SE  = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);

    protected static final AxisAlignedBB AABB_SLAB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public static final PropertyBool     SLOP             = PropertyBool.create("slop");

    public BlockBelt()
    {
        super("belt", Material.IRON, true, false);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(BlockOrientableMachine.FACING, EnumFacing.NORTH).withProperty(BlockBelt.SLOP, false));
    }

    @Override
    public void addCollisionBoxToList(final IBlockState state, final World w, final BlockPos pos,
            final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn,
            final boolean p_185477_7_)
    {
        if (((TileBelt) w.getTileEntity(pos)).isSlope())
        {
            for (final AxisAlignedBB axisalignedbb : BlockBelt.getCollisionBoxList(state))
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
        else
            super.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(w, pos));
    }

    private static List<AxisAlignedBB> getCollisionBoxList(final IBlockState bstate)
    {
        final List<AxisAlignedBB> list = Lists.<AxisAlignedBB> newArrayList();
        list.add(BlockBelt.AABB_SLAB_BOTTOM);
        list.add(BlockBelt.getCollEighthBlock(bstate));
        return list;
    }

    private static AxisAlignedBB getCollEighthBlock(final IBlockState bstate)
    {
        final EnumFacing facing = bstate.getValue(BlockOrientableMachine.FACING);

        switch (facing.getOpposite())
        {
            case NORTH:
            default:
                return BlockBelt.AABB_OCT_TOP_NW;
            case SOUTH:
                return BlockBelt.AABB_OCT_TOP_SE;
            case WEST:
                return BlockBelt.AABB_OCT_TOP_SW;
            case EAST:
                return BlockBelt.AABB_OCT_TOP_NE;
        }
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
    public boolean causesSuffocation(final IBlockState state)
    {
        return false;
    }

    @Override
    public void onEntityWalk(final World w, final BlockPos pos, final Entity e)
    {
        if (((TileBelt) w.getTileEntity(pos)).isWorking())
        {
            final double speed = ((TileBelt) w.getTileEntity(pos)).getBeltSpeed();
            final EnumFacing facing = (EnumFacing) w.getBlockState(pos).getProperties()
                    .get(BlockOrientableMachine.FACING);

            if (facing.getAxis().equals(Axis.X))
                e.motionX += facing.equals(EnumFacing.EAST) ? speed : -speed;
            else
                e.motionZ += facing.equals(EnumFacing.SOUTH) ? speed : -speed;
        }
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
            final BlockPos posNeighbor)
    {
        if (!w.isRemote && posNeighbor.equals(pos.offset(EnumFacing.UP)))
            ((TileBelt) w.getTileEntity(pos)).scanInput();
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        GridManager.getInstance().disconnectCable((TileBelt) w.getTileEntity(pos));

        super.breakBlock(w, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            final IBlockState iblockstate = worldIn.getBlockState(pos.north());
            final IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            final IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            final IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(BlockOrientableMachine.FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(BlockOrientableMachine.FACING, enumfacing), 2);
        }
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        super.onBlockPlacedBy(w, pos, state, placer, stack);
        if (!w.isRemote)
            ((TileBelt) w.getTileEntity(pos)).setFacing(this.getFacing(state));
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        final EnumFacing enumfacing = super.getFacing(meta);
        final boolean slop = (meta >> BlockOrientableMachine.NEEDED_BIT & 1) == 1;

        return this.getDefaultState().withProperty(BlockOrientableMachine.FACING, enumfacing)
                .withProperty(BlockBelt.SLOP, slop);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int meta = state.getValue(BlockBelt.SLOP) ? 1 : 0;
        meta <<= BlockOrientableMachine.NEEDED_BIT;
        meta |= super.getMetaFromState(state);
        return meta;
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileBelt)
        {
            final TileBelt tile = (TileBelt) world.getTileEntity(pos);
            return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, tile.state);
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[] { BlockOrientableMachine.FACING, BlockBelt.SLOP },
                new IUnlistedProperty[] { Properties.AnimationProperty });
    }

    public boolean getSlopState(final IBlockState state)
    {
        return state.getValue(BlockBelt.SLOP).booleanValue();
    }

    public void setSlopState(final World world, final BlockPos pos, final boolean value)
    {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockBelt.SLOP, value));
        ((TileBelt) world.getTileEntity(pos)).setSlope(value);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        super.rotateBlock(world, pos, facing);
        if (!world.isRemote)
        {
            ((TileBelt) world.getTileEntity(pos)).setFacing(facing);
            ((TileBelt) world.getTileEntity(pos)).scanInput();
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileBelt(.05f);
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
            final EnumFacing facing, final IBlockState state)
    {
        if (player.isSneaking())
            this.setSlopState(world, pos, !this.getSlopState(state));
        else
            this.rotateBlock(world, pos, this.getFacing(state).rotateAround(Axis.Y));
        return true;
    }
}