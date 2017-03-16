package net.qbar.common.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.tile.TileStructure;

public class BlockStructure extends BlockMachineBase
{
    public static final PropertyBool MULTIBLOCK_GAG = PropertyBool.create("multiblockgag");

    public BlockStructure()
    {
        super("structure", Material.IRON);

        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStructure.MULTIBLOCK_GAG, false));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(BlockStructure.MULTIBLOCK_GAG) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(BlockStructure.MULTIBLOCK_GAG, meta == 1);
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockStructure.MULTIBLOCK_GAG);
    }

    @Override
    public TileEntity createNewTileEntity(final World w, final int meta)
    {
        final IBlockState state = this.getStateFromMeta(meta);
        if (state.getValue(BlockStructure.MULTIBLOCK_GAG))
            return new TileMultiblockGag();
        return new TileStructure();
    }
}
