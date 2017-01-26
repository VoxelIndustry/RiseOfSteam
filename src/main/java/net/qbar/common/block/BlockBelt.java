package net.qbar.common.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.qbar.common.tile.TileBelt;

public class BlockBelt extends BlockMachineBase
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockBelt()
    {
        super("belt", Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockBelt.FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state,
            final EntityPlayer playerIn, final EnumHand hand, final EnumFacing facing, final float hitX,
            final float hitY, final float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        playerIn.sendMessage(new TextComponentString(
                "FACING : " + worldIn.getBlockState(pos).getProperties().get(BlockBelt.FACING)));
        return true;
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
            EnumFacing enumfacing = state.getValue(BlockBelt.FACING);

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

            worldIn.setBlockState(pos, state.withProperty(BlockBelt.FACING, enumfacing), 2);
        }
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockBelt.FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(BlockBelt.FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(BlockBelt.FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockBelt.FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot)
    {
        return state.withProperty(BlockBelt.FACING, rot.rotate(state.getValue(BlockBelt.FACING)));
    }

    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockBelt.FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] { BlockBelt.FACING });
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileBelt();
    }
}