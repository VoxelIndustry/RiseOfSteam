package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.tile.TilePipeBase;
import net.qbar.common.tile.TileSteamValve;

import javax.annotation.Nullable;

public class BlockSteamValve extends BlockMachineBase<TileSteamValve>
{
    public static final PropertyEnum<EnumFacing.Axis> AXIS   = PropertyEnum.create("axis", EnumFacing.Axis.class);
    public static final PropertyDirection             FACING = PropertyDirection.create("facing",
            facing -> facing.getAxis().isHorizontal());

    public BlockSteamValve()
    {
        super("steamvalve", Material.IRON, TileSteamValve.class);

        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X)
                .withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (!w.isRemote)
        {
            TileSteamValve valve = (TileSteamValve) w.getTileEntity(pos);
            if (valve == null)
                return false;
            valve.setOpen(!valve.isOpen());
        }
        return true;
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
                                final BlockPos posNeighbor)
    {
        if (!w.isRemote)
        {
            BlockPos offset = pos.subtract(posNeighbor);
            ((TilePipeBase<?, ?>) w.getTileEntity(pos)).scanHandler(
                    EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ()));
        }
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        ((TilePipeBase<?, ?>) w.getTileEntity(pos)).disconnectItself();

        super.breakBlock(w, pos, state);
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.VALUES[(meta % 4) + 2];
        EnumFacing.Axis axis = EnumFacing.Axis.values()[meta / 4];

        return this.getDefaultState().withProperty(FACING, facing).withProperty(AXIS, axis);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(FACING).ordinal() - 2) + state.getValue(AXIS).ordinal() * 3;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(AXIS, facing.getAxis())
                .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileSteamValve(64, 1.5f);
    }
}
