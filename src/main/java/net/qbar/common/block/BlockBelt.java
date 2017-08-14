package net.qbar.common.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.qbar.client.render.model.obj.QBarStateProperties;
import net.qbar.common.IWrenchable;
import net.qbar.common.grid.GridManager;
import net.qbar.common.tile.machine.TileBelt;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBelt extends BlockMachineBase<TileBelt> implements IWrenchable
{
    protected static final AxisAlignedBB AABB_OCT_TOP_NW = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D,
            0.5D);
    protected static final AxisAlignedBB AABB_OCT_TOP_NE = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D,
            0.5D);
    protected static final AxisAlignedBB AABB_OCT_TOP_SW = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D,
            1.0D);
    protected static final AxisAlignedBB AABB_OCT_TOP_SE = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D,
            1.0D);

    protected static final AxisAlignedBB AABB_SLAB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D,
            1.0D);

    public static final PropertyEnum<EBeltDirection> FACING   = PropertyEnum.create("facing",
            EBeltDirection.class);
    public static final PropertyEnum<EBeltSlope>     SLOP     = PropertyEnum.create("slope", EBeltSlope.class);
    public static final PropertyBool                 ANIMATED = PropertyBool.create("animated");

    public BlockBelt()
    {
        super("belt", Material.IRON, TileBelt.class);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBelt.FACING, EBeltDirection.NORTH)
                .withProperty(BlockBelt.SLOP, EBeltSlope.NORMAL).withProperty(BlockBelt.ANIMATED, false));
    }

    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileBelt belt = this.getWorldTile(world, pos);

        if (belt != null)
            state = state.withProperty(BlockBelt.ANIMATED, belt.isWorking());
        return state;
    }

    @Override
    public void addCollisionBoxToList(final IBlockState state, final World w, final BlockPos pos,
                                      final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn,
                                      final boolean p_185477_7_)
    {
        if (this.getWorldTile(w, pos).isSlope())
        {
            for (final AxisAlignedBB axisalignedbb : BlockBelt.getCollisionBoxList(state))
                Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
        else
            super.addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(w, pos));
    }

    private static List<AxisAlignedBB> getCollisionBoxList(final IBlockState bstate)
    {
        final List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        list.add(BlockBelt.AABB_SLAB_BOTTOM);
        list.add(BlockBelt.getCollEighthBlock(bstate));
        return list;
    }

    private static AxisAlignedBB getCollEighthBlock(final IBlockState bstate)
    {
        final EBeltDirection facing = bstate.getValue(BlockBelt.FACING);

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
        if (this.getWorldTile(w, pos).isWorking())
        {
            final double speed = ((TileBelt) w.getTileEntity(pos)).getBeltSpeed();
            final EBeltDirection facing = (EBeltDirection) w.getBlockState(pos).getProperties().get(BlockBelt.FACING);

            if (facing.toFacing().getAxis().equals(Axis.X))
                e.motionX += facing.equals(EBeltDirection.EAST) ? speed : -speed;
            else
                e.motionZ += facing.equals(EBeltDirection.SOUTH) ? speed : -speed;
        }
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        GridManager.getInstance().disconnectCable((TileBelt) w.getTileEntity(pos));

        TileBelt belt = this.getWorldTile(w, pos);

        if(!belt.getItems().isEmpty())
            belt.getItems().forEach(item -> InventoryHelper.spawnItemStack(w, pos.getX(), pos.getY(),pos.getZ(), item.getStack()));
        super.breakBlock(w, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(final World world, final BlockPos pos, final IBlockState state)
    {
        if (!world.isRemote)
        {
            final IBlockState iblockstate = world.getBlockState(pos.north());
            final IBlockState iblockstate1 = world.getBlockState(pos.south());
            final IBlockState iblockstate2 = world.getBlockState(pos.west());
            final IBlockState iblockstate3 = world.getBlockState(pos.east());
            EBeltDirection direction = state.getValue(BlockBelt.FACING);

            if (direction == EBeltDirection.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                direction = EBeltDirection.SOUTH;
            }
            else if (direction == EBeltDirection.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                direction = EBeltDirection.NORTH;
            }
            else if (direction == EBeltDirection.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                direction = EBeltDirection.EAST;
            }
            else if (direction == EBeltDirection.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                direction = EBeltDirection.WEST;
            }

            world.setBlockState(pos, state.withProperty(BlockBelt.FACING, direction), 2);
        }
    }

    public EBeltDirection getFacing(final IBlockState state)
    {
        return state.getValue(BlockBelt.FACING);
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
                                            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockBelt.FACING,
                EBeltDirection.fromFacing(placer.getHorizontalFacing().getOpposite()));
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
                                final EntityLivingBase placer, final ItemStack stack)
    {
        w.setBlockState(pos, state.withProperty(BlockBelt.FACING,
                EBeltDirection.fromFacing(placer.getHorizontalFacing().getOpposite())), 2);
        if (!w.isRemote)
            ((TileBelt) w.getTileEntity(pos)).setFacing(this.getFacing(w.getBlockState(pos)).toFacing());
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        final EBeltDirection facing = EBeltDirection.getOrientation(meta % 4);
        final EBeltSlope slop = EBeltSlope.getOrientation(meta / 4);

        return this.getDefaultState().withProperty(BlockBelt.FACING, facing).withProperty(BlockBelt.SLOP, slop);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockBelt.FACING).ordinal() + state.getValue(BlockBelt.SLOP).ordinal() * 4;
    }

    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            final TileBelt tile = this.getWorldTile(world, pos);
            return ((IExtendedBlockState) state).withProperty(QBarStateProperties.VISIBILITY_PROPERTY,
                    tile.getVisibilityState());
        }
        return state;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{BlockBelt.FACING, BlockBelt.SLOP, BlockBelt.ANIMATED},
                new IUnlistedProperty[]{QBarStateProperties.VISIBILITY_PROPERTY});
    }

    public EBeltSlope getSlopState(final IBlockState state)
    {
        return state.getValue(BlockBelt.SLOP);
    }

    public void setSlopState(final World world, final BlockPos pos, final EBeltSlope value)
    {
        final NBTTagCompound tag = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());
        GridManager.getInstance().disconnectCable((TileBelt) world.getTileEntity(pos));
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockBelt.SLOP, value));
        world.getTileEntity(pos).readFromNBT(tag);
        ((TileBelt) world.getTileEntity(pos)).setSlope(value);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        if (facing == null || !EnumFacing.Plane.HORIZONTAL.apply(facing))
            return false;
        NBTTagCompound tag = null;
        if (!world.isRemote)
        {
            tag = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());
            GridManager.getInstance().disconnectCable((TileBelt) world.getTileEntity(pos));
        }

        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockBelt.FACING, EBeltDirection.fromFacing(facing)));

        if (!world.isRemote)
        {
            world.getTileEntity(pos).readFromNBT(tag);
            ((TileBelt) world.getTileEntity(pos)).setFacing(facing);
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
                            final EnumFacing facing, final IBlockState state, ItemStack wrench)
    {
        if (player.isSneaking())
            this.setSlopState(world, pos, this.getSlopState(state).cycle());
        else
            this.rotateBlock(world, pos, this.getFacing(state).toFacing().rotateAround(Axis.Y));
        return true;
    }

    public enum EBeltSlope implements IStringSerializable
    {
        NORMAL, UP, DOWN;

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

        public static EBeltSlope getOrientation(final int value)
        {
            switch (value)
            {
                case 0:
                    return NORMAL;
                case 1:
                    return UP;
                case 2:
                    return DOWN;
            }
            return NORMAL;
        }

        public EBeltSlope cycle()
        {
            switch (this)
            {
                case UP:
                    return NORMAL;
                case NORMAL:
                    return DOWN;
                case DOWN:
                    return UP;
            }
            return NORMAL;
        }

        public boolean isSlope()
        {
            return this != NORMAL;
        }
    }

    public enum EBeltDirection implements IStringSerializable
    {
        NORTH, EAST, SOUTH, WEST;

        public EBeltDirection getOpposite()
        {
            switch (this)
            {
                case EAST:
                    return EBeltDirection.EAST;
                case NORTH:
                    return EBeltDirection.SOUTH;
                case SOUTH:
                    return EBeltDirection.NORTH;
                case WEST:
                    return EBeltDirection.WEST;
                default:
                    return NORTH;
            }
        }

        public static EBeltDirection getOrientation(final int value)
        {
            switch (value)
            {
                case 0:
                    return EBeltDirection.NORTH;
                case 1:
                    return EBeltDirection.EAST;
                case 2:
                    return EBeltDirection.SOUTH;
                default:
                    return EBeltDirection.WEST;
            }
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

        public EnumFacing toFacing()
        {
            switch (this)
            {
                case EAST:
                    return EnumFacing.EAST;
                case NORTH:
                    return EnumFacing.NORTH;
                case SOUTH:
                    return EnumFacing.SOUTH;
                case WEST:
                    return EnumFacing.WEST;
                default:
                    return EnumFacing.NORTH;
            }
        }

        public static EBeltDirection fromFacing(final EnumFacing facing)
        {
            switch (facing)
            {
                case EAST:
                    return EAST;
                case NORTH:
                    return NORTH;
                case SOUTH:
                    return SOUTH;
                case WEST:
                    return WEST;
                default:
                    return NORTH;
            }
        }
    }
}