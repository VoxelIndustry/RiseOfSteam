package net.ros.common.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.tile.TileSteamGauge;

import java.util.function.Supplier;

import static net.minecraft.block.BlockDirectional.FACING;

public class BlockSteamGauge extends BlockPipeBase<TileSteamGauge>
{
    public BlockSteamGauge(String name, double width, Supplier<TileSteamGauge> tileSupplier)
    {
        super(name, width, tileSupplier, TileSteamGauge.class);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
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
}
