package net.qbar.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;
import net.qbar.common.QBarConstants;
import net.qbar.common.tile.QBarTileBase;

public abstract class BlockMachineBase<T extends QBarTileBase> extends BlockContainer implements INamedBlock
{
    public  String   name;
    private Class<T> tileClass;

    public BlockMachineBase(final String name, final Material material, Class<T> tileClass)
    {
        super(material);

        this.name = name;
        this.setRegistryName(QBarConstants.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(QBarConstants.TAB_ALL);
        this.tileClass = tileClass;
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public T getWorldTile(IBlockAccess world, BlockPos pos)
    {
        return (T) this.getRawWorldTile(world, pos);
    }

    public TileEntity getRawWorldTile(IBlockAccess world, BlockPos pos)
    {
        if (world instanceof ChunkCache)
            return  ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        else
            return  world.getTileEntity(pos);
    }

    public boolean checkWorldTile(IBlockAccess world, BlockPos pos)
    {
        if (world instanceof ChunkCache)
            return tileClass.isInstance(((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK));
        else
            return tileClass.isInstance(world.getTileEntity(pos));
    }

    public Class<T> getTileClass()
    {
        return tileClass;
    }
}
