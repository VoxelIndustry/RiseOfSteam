package net.qbar.common.block;

import lombok.Getter;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.qbar.QBar;

public class BlockEnergizedTallGrass extends BlockTallGrass implements INamedBlock
{
    @Getter
    private String name;

    public BlockEnergizedTallGrass(String name)
    {
        this.name = name;

        this.setRegistryName(QBar.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBar.TAB_ALL);

        this.setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return 7;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this);
    }
}
