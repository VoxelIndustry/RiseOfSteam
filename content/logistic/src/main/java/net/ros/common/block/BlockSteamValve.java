package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.init.ROSItems;
import net.ros.common.tile.TilePipeBase;
import net.ros.common.tile.TileSteamValve;

import javax.annotation.Nullable;

public class BlockSteamValve extends BlockPipeBase<TileSteamValve>
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockSteamValve(double width)
    {
        super("steamvalve", width, TileSteamValve::new, TileSteamValve.class);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH)
        {
            if (!w.isRemote)
            {
                TileSteamValve valve = (TileSteamValve) w.getTileEntity(pos);
                if (valve == null)
                    return false;
                valve.setOpen(!valve.isOpen());
            }
            return true;
        }

        return super.onBlockActivated(w, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(final IBlockState state, final World w, final BlockPos pos, final Block block,
                                final BlockPos posNeighbor)
    {
        if (!w.isRemote)
        {
            BlockPos offset = pos.subtract(posNeighbor);
            EnumFacing facing = EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ())
                    .getOpposite();

            TileSteamValve pipe = this.getWorldTile(w, pos);
            pipe.scanHandler(facing);
            pipe.scanValve(facing);
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
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            return ((IExtendedBlockState) state).withProperty(StateProperties.VISIBILITY_PROPERTY,
                    this.getWorldTile(world, pos).getVisibilityState());
        }
        return state;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{FACING},
                new IUnlistedProperty[]{StateProperties.VISIBILITY_PROPERTY});
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.VALUES[meta];

        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileSteamValve(64, 1.5f);
    }
}
