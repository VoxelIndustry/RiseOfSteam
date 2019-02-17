package net.ros.common.block;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.IWrenchable;
import net.voxelindustry.steamlayer.tile.TileBase;

import javax.annotation.Nullable;

public abstract class BlockOrientableMachine<T extends TileBase> extends BlockMachineBase<T> implements IWrenchable
{
    public static final PropertyDirection FACING = BlockDirectional.FACING;

    public static final int NEEDED_BIT = 3;

    private final boolean horizontal;
    private final boolean vertical;

    public BlockOrientableMachine(final String name, final Material material, final boolean horizontal,
                                  final boolean vertical, Class<T> tileClass)
    {
        super(name, material, tileClass);

        this.horizontal = horizontal;
        this.vertical = vertical;

        if (this.horizontal && this.vertical)
            this.setDefaultState(
                    this.blockState.getBaseState().withProperty(BlockOrientableMachine.FACING, EnumFacing.NORTH));
        else if (this.horizontal)
            this.setDefaultState(
                    this.blockState.getBaseState().withProperty(BlockOrientableMachine.FACING, EnumFacing.NORTH));
        else if (this.vertical)
            this.setDefaultState(
                    this.blockState.getBaseState().withProperty(BlockOrientableMachine.FACING, EnumFacing.UP));
    }

    @Nullable
    public EnumFacing getFacing(final int meta)
    {
        final int i = meta & 7;
        EnumFacing result = null;

        if (i > 5)
            return null;

        if (this.horizontal && this.vertical)
            result = EnumFacing.VALUES[i];
        else if (this.horizontal)
        {
            result = EnumFacing.byIndex(i);

            if (result.getAxis() == EnumFacing.Axis.Y)
                result = EnumFacing.NORTH;
        }
        else if (this.vertical)
        {
            result = EnumFacing.byIndex(i);

            if (result.getAxis() == EnumFacing.Axis.X)
                result = EnumFacing.NORTH;
        }
        return result;
    }

    public EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockOrientableMachine.FACING);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockOrientableMachine.FACING, this.getFacing(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockOrientableMachine.FACING).getIndex();
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
                            final EnumFacing facing, final IBlockState state, ItemStack wrench)
    {
        this.rotateBlock(world, pos, this.getFacing(state).rotateAround(Axis.Y));
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockOrientableMachine.FACING);
    }

    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot)
    {
        return state.withProperty(BlockOrientableMachine.FACING,
                rot.rotate(state.getValue(BlockOrientableMachine.FACING)));
    }

    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockOrientableMachine.FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
                                            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        if (this.horizontal && this.vertical)
            return this.getDefaultState().withProperty(BlockOrientableMachine.FACING,
                    EnumFacing.getDirectionFromEntityLiving(pos, placer));
        else if (this.horizontal)
            return this.getDefaultState().withProperty(BlockOrientableMachine.FACING,
                    placer.getHorizontalFacing().getOpposite());
        else if (this.vertical)
            // TODO : Use a method to determine the placer vertical facing
            return this.getDefaultState().withProperty(BlockOrientableMachine.FACING,
                    EnumFacing.getDirectionFromEntityLiving(pos, placer));
        return null;
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        final NBTTagCompound previous = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());
        if (this.horizontal && this.vertical)
        {
            if (facing == null)
                return false;
            world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockOrientableMachine.FACING, facing));
        }
        else if (this.horizontal)
        {
            if (facing == null || !EnumFacing.Plane.HORIZONTAL.apply(facing))
                return false;
            world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockOrientableMachine.FACING, facing));
        }
        else if (this.vertical)
        {
            if (facing == null || !EnumFacing.Plane.VERTICAL.apply(facing))
                return false;
            world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockOrientableMachine.FACING, facing));
        }
        world.getTileEntity(pos).readFromNBT(previous);
        return true;
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
                                final EntityLivingBase placer, final ItemStack stack)
    {
        if (this.horizontal && this.vertical)
            w.setBlockState(pos, state.withProperty(BlockOrientableMachine.FACING,
                    EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
        else if (this.horizontal)
            w.setBlockState(pos,
                    state.withProperty(BlockOrientableMachine.FACING, placer.getHorizontalFacing().getOpposite()), 2);
        else if (this.vertical)
            w.setBlockState(pos, state.withProperty(BlockOrientableMachine.FACING,
                    EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
    }
}
