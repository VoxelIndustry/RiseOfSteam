package net.ros.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.block.property.PropertyString;
import net.ros.common.recipe.MaterialShape;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import org.apache.commons.lang3.StringUtils;

public class BlockScaffold extends BlockMetal
{
    private BlockScaffold(String name, PropertyString variants)
    {
        super(name, variants);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        return true;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public static Builder build(String name)
    {
        return new Builder(name);
    }

    public static class Builder extends BlockMetal.Builder
    {
        public Builder(String name)
        {
            super(name);
        }

        public Builder type(MaterialShape type)
        {
            this.type = type;
            return this;
        }

        public BlockScaffold create()
        {
            PropertyString variants = new PropertyString("variant",
                    Materials.metals.stream().filter(metal ->
                            Materials.metals.containsShape(metal, type) &&
                                    !OreDictionary.doesOreNameExist(type.getOreDict() +
                                            StringUtils.capitalize(metal.getName())))
                            .map(Metal::getName).toArray(String[]::new));
            FAKE_VARIANTS = variants;

            return new BlockScaffold(this.name, variants);
        }
    }
}
