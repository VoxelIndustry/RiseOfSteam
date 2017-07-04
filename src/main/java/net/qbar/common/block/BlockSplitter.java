package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.qbar.QBar;
import net.qbar.common.gui.EGui;
import net.qbar.common.tile.machine.TileSplitter;

public class BlockSplitter extends BlockOrientableMachine<TileSplitter>
{
    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 14 / 16D, 1.0D);
    public static          PropertyBool  FILTER           = PropertyBool.create("filter");

    public BlockSplitter()
    {
        super("itemsplitter", Material.IRON, true, false, TileSplitter.class);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(BlockOrientableMachine.FACING, EnumFacing.NORTH)
                        .withProperty(BlockSplitter.FILTER, false));
    }

    @Override
    public void getSubBlocks(final CreativeTabs tab, final NonNullList<ItemStack> stacks)
    {
        if (tab == this.getCreativeTabToDisplayOn())
        {
            stacks.add(new ItemStack(this, 1, 0));
            stacks.add(new ItemStack(this, 1, 1));
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockOrientableMachine.FACING, BlockSplitter.FILTER);
    }

    @Override
    public boolean onBlockActivated(final World w, final BlockPos pos, final IBlockState state,
                                    final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY,
                                    final float hitZ)
    {
        if (player.isSneaking())
            return false;
        final TileSplitter splitter = (TileSplitter) w.getTileEntity(pos);
        if (splitter != null && splitter.hasFilter())
        {
            player.openGui(QBar.instance, EGui.SPLITTER.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos)
    {
        return BlockSplitter.AABB_BOTTOM_HALF;
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
    public int damageDropped(final IBlockState state)
    {
        return state.getValue(BlockSplitter.FILTER) ? 1 : 0;
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(BlockOrientableMachine.FACING).getIndex();

        if (state.getValue(BlockSplitter.FILTER).booleanValue())
            i |= 8;
        return i;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockOrientableMachine.FACING, this.getFacing(meta))
                .withProperty(BlockSplitter.FILTER, Boolean.valueOf((meta & 8) > 0));
    }

    @Override
    public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing,
                                            final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer)
    {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(BlockSplitter.FILTER, meta == 1);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        super.rotateBlock(world, pos, facing);
        if (!world.isRemote)
            ((TileSplitter) world.getTileEntity(pos)).setFacing(facing);
        return true;
    }

    @Override
    public void onBlockPlacedBy(final World w, final BlockPos pos, final IBlockState state,
                                final EntityLivingBase placer, final ItemStack stack)
    {
        super.onBlockPlacedBy(w, pos, state, placer, stack);
        if (!w.isRemote)
            ((TileSplitter) w.getTileEntity(pos)).setFacing(this.getFacing(state));
    }

    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state)
    {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSplitter(this.getStateFromMeta(meta).getValue(BlockSplitter.FILTER));
    }
}
