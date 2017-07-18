package net.qbar.common.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.block.property.PropertyString;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.SludgeData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockVeinOre extends BlockBase implements IModelProvider
{
    @Getter
    private List<QBarOre> contents;

    private static PropertyString FAKE_VARIANTS;
    @Getter
    private final  PropertyString VARIANTS;

    public BlockVeinOre(String name, String defaultValue, List<QBarOre> contents, PropertyString variants)
    {
        super(name, Material.ROCK);

        this.VARIANTS = variants;
        this.contents = contents;

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, defaultValue));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
    {
        return this.contents.get(this.getMetaFromState(world.getBlockState(pos))).getResistance() / 5.0F;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
        return this.contents.get(this.getMetaFromState(state)).getHardness();
    }

    @Override
    public int getHarvestLevel(IBlockState state)
    {
        return contents.get(this.getMetaFromState(state)).getToolLevel();
    }

    @Override
    public String getHarvestTool(IBlockState state)
    {
        return "pickaxe";
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == this.getCreativeTabToDisplayOn())
        {
            for (int i = 0; i < contents.size(); i++)
                items.add(new ItemStack(this, 1, i));
        }
    }

    public IBlockState getStateFromOre(String oreName)
    {
        return this.getDefaultState().withProperty(VARIANTS, oreName);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(VARIANTS, VARIANTS.getByIndex(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return VARIANTS.indexOf(state.getValue(VARIANTS));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FAKE_VARIANTS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getItemModelCount()
    {
        return this.getVARIANTS().getAllowedValues().size();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemModelFromMeta(int itemMeta)
    {
        return "ores=" + this.getStateFromMeta(itemMeta).getValue(VARIANTS);
    }

    public static class Builder
    {
        private List<QBarOre> contents = new ArrayList<>();
        private String name;
        private String defaultValue;

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder addContent(QBarOre variant)
        {
            if (defaultValue == null)
                this.defaultValue = variant.getName();
            this.contents.add(variant);
            return this;
        }

        public BlockVeinOre create()
        {
            PropertyString variants = new PropertyString("ores");
            variants.addValues(contents.stream().map(QBarOre::getName).toArray(String[]::new));
            FAKE_VARIANTS = variants;

            return new BlockVeinOre(name, defaultValue, contents, variants);
        }
    }
}
