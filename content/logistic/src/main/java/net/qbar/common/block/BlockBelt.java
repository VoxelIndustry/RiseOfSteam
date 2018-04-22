package net.qbar.common.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.qbar.client.render.model.obj.QBarStateProperties;
import net.qbar.common.IWrenchable;
import net.qbar.common.block.property.BeltDirection;
import net.qbar.common.block.property.BeltProperties;
import net.qbar.common.block.property.BeltSlope;
import net.qbar.common.grid.GridManager;
import net.qbar.common.grid.ItemBelt;
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



    public BlockBelt()
    {
        super("belt", Material.IRON, TileBelt.class);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BeltProperties.FACING, BeltDirection.NORTH)
                .withProperty(BeltProperties.SLOP, BeltSlope.NORMAL).withProperty(BeltProperties.ANIMATED, false));
    }

    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileBelt belt = this.getWorldTile(world, pos);

        if (belt != null)
            state = state.withProperty(BeltProperties.ANIMATED, belt.isWorking());
        return state;
    }

    @Override
    public void addCollisionBoxToList(final IBlockState state, final World w, final BlockPos pos,
                                      final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes,
                                      @Nullable final Entity entityIn,
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
        final BeltDirection facing = bstate.getValue(BeltProperties.FACING);

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
            final BeltDirection facing = (BeltDirection) w.getBlockState(pos).getProperties().get(BeltProperties.FACING);

            if (facing.toFacing().getAxis().equals(Axis.X))
                e.motionX += facing.equals(BeltDirection.EAST) ? speed : -speed;
            else
                e.motionZ += facing.equals(BeltDirection.SOUTH) ? speed : -speed;
        }
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        GridManager.getInstance().disconnectCable((TileBelt) w.getTileEntity(pos));

        TileBelt belt = this.getWorldTile(w, pos);

        if (!belt.isEmpty())
        {
            for (ItemBelt itemBelt : belt.getItems())
            {
                if (itemBelt != null)
                    InventoryHelper.spawnItemStack(w, pos.getX(), pos.getY(), pos.getZ(), itemBelt.getStack());
            }
        }
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
            BeltDirection direction = state.getValue(BeltProperties.FACING);

            if (direction == BeltDirection.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                direction = BeltDirection.SOUTH;
            }
            else if (direction == BeltDirection.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                direction = BeltDirection.NORTH;
            }
            else if (direction == BeltDirection.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                direction = BeltDirection.EAST;
            }
            else if (direction == BeltDirection.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                direction = BeltDirection.WEST;
            }

            world.setBlockState(pos, state.withProperty(BeltProperties.FACING, direction), 2);
        }
    }

    public BeltDirection getFacing(final IBlockState state)
    {
        return state.getValue(BeltProperties.FACING);
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
                                            final float hitX, final float hitY, final float hitZ, final int meta,
                                            final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BeltProperties.FACING,
                BeltDirection.fromFacing(placer.getHorizontalFacing().getOpposite()));
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
                                final EntityLivingBase placer, final ItemStack stack)
    {
        w.setBlockState(pos, state.withProperty(BeltProperties.FACING,
                BeltDirection.fromFacing(placer.getHorizontalFacing().getOpposite())), 2);
        if (!w.isRemote)
            ((TileBelt) w.getTileEntity(pos)).setFacing(this.getFacing(w.getBlockState(pos)).toFacing());
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        final BeltDirection facing = BeltDirection.getOrientation(meta % 4);
        final BeltSlope slop = BeltSlope.getOrientation(meta / 4);

        return this.getDefaultState().withProperty(BeltProperties.FACING, facing).withProperty(BeltProperties.SLOP, slop);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BeltProperties.FACING).ordinal() + state.getValue(BeltProperties.SLOP).ordinal() * 4;
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
        return new ExtendedBlockState(this, new IProperty[]{BeltProperties.FACING, BeltProperties.SLOP, BeltProperties.ANIMATED},
                new IUnlistedProperty[]{QBarStateProperties.VISIBILITY_PROPERTY});
    }

    public BeltSlope getSlopState(final IBlockState state)
    {
        return state.getValue(BeltProperties.SLOP);
    }

    public void setSlopState(final World world, final BlockPos pos, final BeltSlope value)
    {
        final NBTTagCompound tag = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());
        GridManager.getInstance().disconnectCable((TileBelt) world.getTileEntity(pos));
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BeltProperties.SLOP, value));
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

        world.setBlockState(pos, world.getBlockState(pos).withProperty(BeltProperties.FACING, BeltDirection.fromFacing
                (facing)));

        if (!world.isRemote)
        {
            world.getTileEntity(pos).readFromNBT(tag);
            ((TileBelt) world.getTileEntity(pos)).setFacing(facing);
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (facing != EnumFacing.UP)
            return false;

        TileBelt belt = this.getWorldTile(w, pos);

        float posX = belt.getFacing().getAxis() == Axis.X ? hitZ : hitX;
        float posY = belt.getFacing().getAxis() == Axis.X ? hitX : hitZ;

        if (belt.getFacing() == EnumFacing.WEST)
        {
            posX = 1 - posX;
            posY = 1 - posY;
        }
        else if (belt.getFacing() == EnumFacing.NORTH)
            posY = 1 - posY;
        else if (belt.getFacing() == EnumFacing.SOUTH)
            posX = 1 - posX;

        if (!w.isRemote && belt.hasGrid())
        {
            if (!player.isSneaking())
            {
                if (!player.getHeldItemMainhand().isEmpty() && belt.insert(player.getHeldItemMainhand(), posX, posY,
                        false))
                {
                    ItemStack copy = player.getHeldItemMainhand().copy();
                    copy.setCount(1);
                    player.getHeldItemMainhand().shrink(1);
                    belt.insert(copy, posX, posY, true);
                    belt.itemUpdate();
                }
            }
            else
            {
                // Used to catch item stopped right at the limit of the belt
                posY += 1 / 16F;
                for (int i = 0; i < belt.getItems().length; i++)
                {
                    ItemBelt itemBelt = belt.getItems()[i];
                    if (itemBelt == null)
                        continue;
                    if (posX >= itemBelt.getPosX() && posX <= itemBelt.getPosX() + 6 / 16F && posY >= itemBelt.getPosY()
                            && posY <= itemBelt.getPosY() + 6 / 16F)
                    {
                        player.addItemStackToInventory(itemBelt.getStack());
                        belt.getItems()[i] = null;
                        belt.itemUpdate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileBelt(.1f);
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
}