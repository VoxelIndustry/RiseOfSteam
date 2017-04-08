package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.qbar.common.IWrenchable;
import net.qbar.common.tile.machine.TileSolarMirror;

public class BlockSolarMirror extends BlockMachineBase implements IWrenchable
{
    public BlockSolarMirror()
    {
        super("solar_mirror", Material.GLASS);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileSolarMirror)
        {
            TileSolarMirror solarMirror = (TileSolarMirror) world.getTileEntity(pos);
            if (solarMirror.transform != TRSRTransformation.identity())
                return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, solarMirror.transform);
        }
        return state;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { Properties.AnimationProperty });
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
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSolarMirror();
    }

    @Override
    public boolean onWrench(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
            IBlockState state, ItemStack wrench)
    {
        float angle = ((TileSolarMirror) world.getTileEntity(pos)).getHorizontalAngle();

        if (player.isSneaking())
            angle -= 10;
        else
            angle += 10;
        angle %= 360;
        ((TileSolarMirror) world.getTileEntity(pos)).setHorizontalAngle(angle);
        return false;
    }
}
