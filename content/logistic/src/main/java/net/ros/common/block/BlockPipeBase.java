package net.ros.common.block;

import com.google.common.collect.EnumHashBiMap;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.ros.client.AABBRaytracer;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.ROSConstants;
import net.ros.common.grid.node.IBlockPipe;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeType;
import net.ros.common.init.ROSBlocks;
import net.ros.common.init.ROSItems;
import net.ros.common.tile.TilePipeBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BlockPipeBase<T extends TilePipeBase> extends BlockMachineBase<T> implements IComplexSelectBox, IBlockPipe
{
    private final EnumHashBiMap<EnumFacing, AxisAlignedBB> boxes;
    protected final AxisAlignedBB                            BOX_NONE;
    private final Function<PipeType, T>                    tileSupplier;
    @Getter
    private final PipeType                                 pipeType;

    public BlockPipeBase(String name, double width, PipeType type, Function<PipeType, T> tileSupplier,
                         Class<T> tileClass)
    {
        super(name, Material.IRON, tileClass);

        this.setCreativeTab(ROSConstants.TAB_PIPES);

        this.tileSupplier = tileSupplier;
        this.boxes = EnumHashBiMap.create(EnumFacing.class);
        this.pipeType = type;

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
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);

        tooltip.add("Rate: " + TextFormatting.AQUA + PipeType.getTransferRate(pipeType) + " mB/T");

        if (pipeType.getNature() == PipeNature.STEAM)
            tooltip.add("Max Pressure: " + TextFormatting.GOLD + PipeType.getPressure(pipeType));
        else if (pipeType.getNature() == PipeNature.FLUID)
            tooltip.add("Max Heat: " + TextFormatting.RED + PipeType.getHeat(pipeType) + " K");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TilePipeBase<?, ?> tile = (TilePipeBase<?, ?>) source.getTileEntity(pos);
        AxisAlignedBB res = BOX_NONE;
        if (tile != null)
        {
            for (EnumFacing facing: EnumFacing.VALUES)
            {
                if (tile.isConnected(facing) || tile.isConnectionForbidden(facing))
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
            return ((IExtendedBlockState) state).withProperty(StateProperties.VISIBILITY_PROPERTY,
                    this.getWorldTile(world, pos).getVisibilityState());
        }
        return state;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[0],
                new IUnlistedProperty[]{StateProperties.VISIBILITY_PROPERTY});
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block,
                                BlockPos posNeighbor)
    {
        if (!w.isRemote)
        {
            BlockPos offset = pos.subtract(posNeighbor);
            EnumFacing facing = EnumFacing.getFacingFromVector(offset.getX(), offset.getY(), offset.getZ())
                    .getOpposite();

            T pipe = this.getWorldTile(w, pos);
            pipe.scanHandler(facing);
        }
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
        if (player.isSneaking())
            return false;

        if (player.getHeldItemMainhand().getItem() == ROSItems.VALVE)
        {
            if (world.isRemote)
                return true;

            if (this == ROSBlocks.STEAM_PIPE_SMALL)
                world.setBlockState(pos, ROSBlocks.STEAM_VALVE_BRASS_SMALL.getDefaultState()
                        .withProperty(BlockDirectional.FACING, facing));
            else if (this == ROSBlocks.FLUID_PIPE_SMALL)
                world.setBlockState(pos, ROSBlocks.FLUID_VALVE_IRON_SMALL.getDefaultState()
                        .withProperty(BlockDirectional.FACING, facing));

            if (!player.isCreative())
                player.getHeldItemMainhand().shrink(1);
            return true;
        }

        if (player.getHeldItemMainhand().getItem() == ROSItems.GAUGE && this == ROSBlocks.STEAM_PIPE_SMALL)
        {
            if (world.isRemote)
                return true;

            world.setBlockState(pos, ROSBlocks.STEAM_GAUGE_BRASS_SMALL.getDefaultState()
                    .withProperty(BlockDirectional.FACING, facing));

            if (!player.isCreative())
                player.getHeldItemMainhand().shrink(1);
            return true;
        }


        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH)
            return false;
        AxisAlignedBB box = this.getSelectedBox(player, pos, 0);

        if (box == null)
            return false;
        box = box.offset(new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ()));

        if (!boxes.containsValue(box) && !BOX_NONE.equals(box))
            return false;

        if (!world.isRemote)
        {
            TilePipeBase pipe = this.getWorldTile(world, pos);

            if (box == BOX_NONE)
            {
                // The forbidden flag does not matter here, the tile keep its own
                pipe.forbidConnection(null, true);
            }
            else
            {
                EnumFacing target = boxes.inverse().get(box);

                pipe.forbidConnection(target, !pipe.isConnectionForbidden(target));
            }
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return tileSupplier.apply(this.getPipeType());
    }

    @Override
    public AxisAlignedBB getSelectedBox(EntityPlayer player, BlockPos pos, float partialTicks)
    {
        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH)
            return this.getBoundingBox(player.world.getBlockState(pos), player.world, pos).offset(pos);

        TilePipeBase pipe = this.getWorldTile(player.world, pos);
        List<AxisAlignedBB> candidates = new ArrayList<>();

        candidates.add(BOX_NONE.offset(pos));

        for (EnumFacing facing: EnumFacing.VALUES)
        {
            if (pipe.isConnected(facing) || pipe.isConnectionForbidden(facing))
                candidates.add(this.boxes.get(facing).offset(pos));
        }

        getAdditionalBox(player, pos, player.world.getBlockState(pos)).ifPresent(candidates::add);

        Optional<AxisAlignedBB> box = AABBRaytracer.raytraceClosest(player, partialTicks,
                candidates.toArray(new AxisAlignedBB[0]));

        return box.orElse(null);
    }

    protected Optional<AxisAlignedBB> getAdditionalBox(EntityPlayer player, BlockPos pos, IBlockState state)
    {
        return Optional.empty();
    }
}
