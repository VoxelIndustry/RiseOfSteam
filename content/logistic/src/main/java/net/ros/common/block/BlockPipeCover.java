package net.ros.common.block;

import fr.ourten.teabeans.function.PetaFunction;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.block.item.IItemBlockProvider;
import net.ros.common.block.item.ItemBlockPipeCover;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.grid.node.PipeType;
import net.ros.common.init.ROSItems;
import net.ros.common.multiblock.BlockMultiblockBase;
import net.ros.common.multiblock.RightClickAction;
import net.ros.common.tile.TilePipeBase;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.block.BlockDirectional.FACING;

public class BlockPipeCover<T extends TilePipeBase> extends BlockPipeBase<T> implements IItemBlockProvider
{
    private RightClickAction                   onRightClick;
    private EnumMap<EnumFacing, AxisAlignedBB> boxes;

    @Getter
    private PipeCoverType coverType;

    public BlockPipeCover(String name, double width, PipeType type, Function<PipeType, T> tileSupplier,
                          Class<T> tileClass, PipeCoverType coverType, RightClickAction onRightClick, AxisAlignedBB box)
    {
        super(name.replace("pipe", coverType.getPrefix()), width, type, tileSupplier, tileClass);

        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
                .withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false));

        this.coverType = coverType;
        this.onRightClick = onRightClick;

        this.boxes = new EnumMap<>(EnumFacing.class);

        if (type.getSize().ordinal() >= PipeSize.LARGE.ordinal())
        {
            this.boxes.put(EnumFacing.NORTH, new AxisAlignedBB(box.minX, box.minY, 1 - box.maxZ,
                    box.maxX, box.maxY, 1 - box.minZ));
            this.boxes.put(EnumFacing.SOUTH, new AxisAlignedBB(1 - box.minX, box.minY, box.minZ,
                    1 - box.maxX, box.maxY, box.maxZ));

            this.boxes.put(EnumFacing.EAST, new AxisAlignedBB(box.minZ, box.minY, box.minX,
                    box.maxZ, box.maxY, box.maxX));
            this.boxes.put(EnumFacing.WEST, new AxisAlignedBB(1 - box.maxZ, box.minY, 1 - box.minX,
                    1 - box.minZ, box.maxY, 1 - box.maxX));

            this.boxes.put(EnumFacing.UP, new AxisAlignedBB(box.minX, box.minZ, box.minY,
                    box.maxX, box.maxZ, box.maxY));
            this.boxes.put(EnumFacing.DOWN, new AxisAlignedBB(box.minX, 1 - box.maxZ, box.minY,
                    box.maxX, 1 - box.minZ, box.maxY));
        }
        else
        {
            this.boxes.put(EnumFacing.NORTH, new AxisAlignedBB(1 - box.minX, box.minY, 1 - box.maxZ,
                    1 - box.maxX, box.maxY, 1 - box.minZ));
            this.boxes.put(EnumFacing.SOUTH, box);

            this.boxes.put(EnumFacing.EAST, new AxisAlignedBB(box.minZ, box.minY, 1 - box.minX,
                    box.maxZ, box.maxY, 1 - box.maxX));
            this.boxes.put(EnumFacing.WEST, new AxisAlignedBB(1 - box.maxZ, box.minY, box.minX,
                    1 - box.minZ, box.maxY, box.maxX));

            this.boxes.put(EnumFacing.UP, new AxisAlignedBB(box.minX, box.minZ, box.minY,
                    box.maxX, box.maxZ, box.maxY));
            this.boxes.put(EnumFacing.DOWN, new AxisAlignedBB(box.minX, 1 - box.maxZ, box.minY,
                    box.maxX, 1 - box.minZ, box.maxY));
        }
    }

    public static <T extends TilePipeBase> PetaFunction<String, Double, PipeType, Function<PipeType, T>, Class<T>,
            BlockPipeCover<T>> getSupplier(PipeCoverType coverType, RightClickAction onRightClick, AxisAlignedBB box)
    {
        return (name, width, type, tileSupplier, tileClass) ->
                new BlockPipeCover<>(name, width, type, tileSupplier, tileClass, coverType, onRightClick, box);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return EnumBlockRenderType.INVISIBLE;
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH)
            return this.onRightClick.apply(w, pos, state, player, hand, facing, hitX, hitY, hitZ);

        return super.onBlockActivated(w, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{FACING, BlockMultiblockBase.MULTIBLOCK_GAG},
                new IUnlistedProperty[]{StateProperties.VISIBILITY_PROPERTY});
    }

    public static EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockMultiblockBase.FACING);
    }

    @Nullable
    public static EnumFacing getFacing(final int meta)
    {
        final int i = meta & 7;
        return i > 5 ? null : EnumFacing.byIndex(i);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta))
                .withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            i |= 8;
        return i;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer)
    {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(w, pos, state, placer, stack);

        if (this.getPipeType().getSize() != PipeSize.LARGE)
            return;

        w.setBlockState(pos.offset(state.getValue(FACING)),
                this.getDefaultState().withProperty(FACING, state.getValue(FACING))
                        .withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, true));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        if (this.getStateFromMeta(meta).getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return null;
        else
            return super.createNewTileEntity(world, meta);
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
        {
            Block potentialCore = w.getBlockState(pos.offset(state.getValue(FACING).getOpposite())).getBlock();

            if (potentialCore instanceof BlockPipeBase)
                w.destroyBlock(pos.offset(state.getValue(FACING).getOpposite()), true);
        }
        else
        {
            if (this.getPipeType().getSize() == PipeSize.LARGE)
                w.destroyBlock(pos.offset(state.getValue(FACING)), false);

            super.breakBlock(w, pos, state);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return this.boxes.get(state.getValue(FACING));
        if (this.getPipeType().getSize() == PipeSize.LARGE)
            return this.BOX_NONE;
        return super.getBoundingBox(state, source, pos).union(this.boxes.get(state.getValue(FACING).getOpposite()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBox(EntityPlayer player, BlockPos pos, float partialTicks)
    {
        if (player.getHeldItemMainhand().getItem() != ROSItems.WRENCH ||
                this.getPipeType().getSize() == PipeSize.LARGE)
            return this.getBoundingBox(player.world.getBlockState(pos), player.world, pos).offset(pos);
        return super.getSelectedBox(player, pos, partialTicks);
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block,
                                BlockPos posNeighbor)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return;
        super.neighborChanged(state, w, pos, block, posNeighbor);
    }

    @Override
    protected Optional<AxisAlignedBB> getAdditionalBox(EntityPlayer player, BlockPos pos, IBlockState state)
    {
        return Optional.of(boxes.get(state.getValue(FACING).getOpposite()).offset(pos));
    }

    @Override
    public ItemBlock getItemBlock()
    {
        if (this.getPipeType().getSize().ordinal() < PipeSize.LARGE.ordinal())
            return new ItemBlock(this);
        return new ItemBlockPipeCover(this);
    }
}
