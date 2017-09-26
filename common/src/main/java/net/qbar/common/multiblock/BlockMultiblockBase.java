package net.qbar.common.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.IWrenchable;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.tile.QBarTileBase;

import javax.annotation.Nullable;

public abstract class BlockMultiblockBase<T extends QBarTileBase & ITileMultiblockCore> extends BlockMachineBase<T>
        implements IWrenchable
{
    public static final PropertyBool      MULTIBLOCK_GAG = PropertyBool.create("multiblockgag");
    public static final PropertyDirection FACING         = BlockHorizontal.FACING;

    private MultiblockComponent           multiblock;

    public BlockMultiblockBase(final String name, final Material material, Class<T> tileClass)
    {
        super(name, material, tileClass);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false)
                .withProperty(BlockMultiblockBase.FACING, EnumFacing.NORTH));
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
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

    public IBlockState getGhostState(final IBlockState state, final VisibilityModelState visibilityState)
    {
        return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, visibilityState);
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return EnumBlockRenderType.INVISIBLE;
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    public static EnumFacing getFacing(final int meta)
    {
        final int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public static EnumFacing getFacing(final IBlockState state)
    {
        return state.getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockMultiblockBase.FACING, BlockMultiblockBase.getFacing(meta))
                .withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, Boolean.valueOf((meta & 8) > 0));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(BlockMultiblockBase.FACING).getIndex();

        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG).booleanValue())
            i |= 8;
        return i;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this,
                new IProperty[] { BlockMultiblockBase.MULTIBLOCK_GAG, BlockMultiblockBase.FACING },
                new IUnlistedProperty[] { Properties.AnimationProperty });
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World w, final BlockPos pos)
    {
        if (!state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return this.getMultiblock().getBox(BlockMultiblockBase.getFacing(state)).offset(pos);
        if (w.getTileEntity(pos) instanceof ITileMultiblock)
        {
            final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
            if (tile != null && !tile.isCore())
                return w.getBlockState(tile.getCorePos()).getSelectedBoundingBox(w, tile.getCorePos());
        }
        return Block.FULL_BLOCK_AABB.offset(pos);
    }

    public boolean canPlaceBlockAt(final World w, final BlockPos pos, final EnumFacing facing)
    {
        final Iterable<BlockPos> searchables = this.getMultiblock().getAllInBox(pos, facing);

        for (final BlockPos current : searchables)
        {
            if (!w.getBlockState(current).getBlock().isReplaceable(w, current))
                return false;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
            final EntityLivingBase placer, final ItemStack stack)
    {
        final Iterable<BlockPos> searchables = this.getMultiblock().getAllInBox(pos, BlockMultiblockBase.getFacing(state));

        for (final BlockPos current : searchables)
        {
            if (!current.equals(pos))
            {
                final IBlockState previous = w.getBlockState(current);
                w.setBlockState(current, this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, true));
                w.notifyBlockUpdate(current, previous, w.getBlockState(current), 3);
                final TileMultiblockGag gag = (TileMultiblockGag) w.getTileEntity(current);
                if (gag != null)
                    gag.setCorePos(pos);
            }
        }
    }

    @Override
    public void breakBlock(final World w, final BlockPos pos, final IBlockState state)
    {
        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
        if (tile != null)
        {
            if (tile.isCore())
            {
                if (tile instanceof IInventory)
                {
                    InventoryHelper.dropInventoryItems(w, pos, (IInventory) tile);
                    w.updateComparatorOutputLevel(pos, this);
                }
                this.dropBlockAsItem(w, pos, state, 0);
            }
            else
                tile.breakCore();
        }
        super.breakBlock(w, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block, BlockPos from)
    {
        super.neighborChanged(state, w, pos, block, from);

        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);
        if (tile != null && !tile.isCore() && !tile.isCorePresent())
            w.getTileEntity(pos).getWorld().destroyBlock(pos, false);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
            final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
            final float hitZ)
    {
        final ITileMultiblock tile = (ITileMultiblock) w.getTileEntity(pos);

        if (tile != null && tile.getCore() != null)
            return tile.getCore().onRightClick(player, facing, hitX, hitY, hitZ, tile.getCoreOffset());
        return false;
    }

    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot)
    {
        return state.withProperty(BlockMultiblockBase.FACING, rot.rotate(state.getValue(BlockMultiblockBase.FACING)));
    }

    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockMultiblockBase.FACING)));
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(BlockMultiblockBase.MULTIBLOCK_GAG, false)
                .withProperty(BlockMultiblockBase.FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
            final EnumFacing facing, final IBlockState state, ItemStack wrench)
    {
        if (player.isSneaking())
            this.getWorldTile(world, pos).breakCore();
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (this.getWorldTile(world, pos).isCore())
        {
            if (QBarMachines.contains(Blueprint.class, this.getMultiblock().getDescriptor().getName()))
                drops.add(QBarItems.MULTIBLOCK_BOX.getBox(this.getMultiblock().getDescriptor().get(Blueprint.class)));
            else
                drops.add(new ItemStack(this));
        }
    }

    @Override
    public TileEntity createNewTileEntity(final World w, final int meta)
    {
        final IBlockState state = this.getStateFromMeta(meta);
        if (state.getValue(BlockMultiblockBase.MULTIBLOCK_GAG))
            return new TileMultiblockGag();
        return this.getTile(w, state);
    }

    public abstract T getTile(final World w, final IBlockState state);

    public MultiblockComponent getMultiblock()
    {
        if (this.multiblock == null)
            this.multiblock = QBarMachines.getComponent(MultiblockComponent.class, this.name);
        return this.multiblock;
    }
}
