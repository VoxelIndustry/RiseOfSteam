package net.ros.common.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.grid.node.PipeType;
import net.ros.common.init.ROSItems;
import net.ros.common.tile.TilePressureValve;

import java.util.function.Function;

import static net.minecraft.block.BlockDirectional.FACING;

public class BlockPressureValve extends BlockPipeBase<TilePressureValve>
{
    public BlockPressureValve(String name, double width, PipeType type,
                              Function<PipeType, TilePressureValve> tileSupplier, Class<TilePressureValve> tileClass)
    {
        super(name.replace("pipe", "pressurevalve"), width, type, tileSupplier, tileClass);
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH)
            return false;

        return super.onBlockActivated(w, pos, state, player, hand, facing, hitX, hitY, hitZ);
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
