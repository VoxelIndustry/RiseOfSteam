package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.qbar.client.AABBRaytracer;
import net.qbar.client.render.model.obj.QBarStateProperties;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.TilePipeBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class BlockPipeBase<T extends TilePipeBase> extends BlockMachineBase<T> implements IComplexSelectBox
{
    private final EnumMap<EnumFacing, AxisAlignedBB> boxes;
    private final AxisAlignedBB                      BOX_NONE;
    private final Supplier<T>                        tileSupplier;

    public BlockPipeBase(String name, double width, Supplier<T> tileSupplier, Class<T> tileClass)
    {
        super(name, Material.IRON, tileClass);

        this.tileSupplier = tileSupplier;
        this.boxes = new EnumMap<>(EnumFacing.class);

        BOX_NONE = new AxisAlignedBB(0.5 - width / 2, 0.5 - width / 2, 0.5 - width / 2,
                0.5 + width / 2, 0.5 + width / 2, 0.5 + width / 2);

        double length = (1 - width) / 2;
        this.boxes.put(EnumFacing.EAST, new AxisAlignedBB(1 - length, 0.5 - width / 2, 0.5 - width / 2,
                1, 0.5 + width / 2, 0.5 + width / 2));
        this.boxes.put(EnumFacing.WEST, new AxisAlignedBB(0, 0.5 - width / 2, 0.5 - width / 2,
                length, 0.5 + width / 2, 0.5 + width / 2));
        this.boxes.put(EnumFacing.SOUTH, new AxisAlignedBB(0.5 - width / 2, 0.5 - width / 2, 1 - length,
                0.5 + width / 2, 0.5 + width / 2, 1));
        this.boxes.put(EnumFacing.NORTH, new AxisAlignedBB(0.5 - width / 2, 0.5 - width / 2, 0,
                0.5 + width / 2, 0.5 + width / 2, length));
        this.boxes.put(EnumFacing.UP, new AxisAlignedBB(0.5 - width / 2, 1 - length, 0.5 - width / 2,
                0.5 + width / 2, 1, 0.5 + width / 2));
        this.boxes.put(EnumFacing.DOWN, new AxisAlignedBB(0.5 - width / 2, 0, 0.5 - width / 2,
                0.5 + width / 2, length, 0.5 + width / 2));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TilePipeBase<?, ?> tile = (TilePipeBase<?, ?>) source.getTileEntity(pos);
        AxisAlignedBB res = BOX_NONE;
        if (tile != null)
        {
            for (EnumFacing facing : EnumFacing.VALUES)
            {
                if (tile.isConnected(facing))
                    res = res.union(this.boxes.get(facing));
            }
        }
        return res;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (this.checkWorldTile(world, pos))
        {
            return ((IExtendedBlockState) state).withProperty(QBarStateProperties.VISIBILITY_PROPERTY,
                    this.getWorldTile(world, pos).getVisibilityState());
        }
        return state;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0],
                new IUnlistedProperty[]{QBarStateProperties.VISIBILITY_PROPERTY});
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block,
                                BlockPos posNeighbor)
    {
        if (!w.isRemote)
            ((TilePipeBase<?, ?>) w.getTileEntity(pos)).scanHandlers(posNeighbor);
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        ((TilePipeBase<?, ?>) w.getTileEntity(pos)).disconnectItself();

        super.breakBlock(w, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking() || player.getHeldItemMainhand().getItem() != QBarItems.WRENCH)
            return false;
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return tileSupplier.get();
    }

    @Override
    public AxisAlignedBB getSelectedBox(EntityPlayer player, BlockPos pos, float partialTicks)
    {
        if (player.getHeldItemMainhand().getItem() != QBarItems.WRENCH)
            return this.getBoundingBox(player.world.getBlockState(pos), player.world, pos).offset(pos);

        TilePipeBase pipe = this.getWorldTile(player.world, pos);
        List<AxisAlignedBB> candidates = new ArrayList<>();

        candidates.add(BOX_NONE.offset(pos));

        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (pipe.isConnected(facing))
                candidates.add(this.boxes.get(facing).offset(pos));
        }

        Optional<AxisAlignedBB> box = AABBRaytracer.raytraceClosest(player, partialTicks,
                candidates.toArray(new AxisAlignedBB[0]));

        return box.orElse(null);
    }
}
