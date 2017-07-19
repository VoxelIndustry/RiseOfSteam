package net.qbar.common.block;

import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.block.property.PropertyString;
import net.qbar.common.ore.QBarOre;
import net.qbar.common.ore.QBarOres;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockVeinOre extends BlockBase implements IModelProvider
{
    public static final PropertyEnum<Richness> RICHNESS = PropertyEnum.create("richness", Richness.class);
    private static PropertyString              FAKE_VARIANTS;

    @Getter
    private final PropertyString               VARIANTS;

    public BlockVeinOre(String name, String defaultValue, PropertyString variants)
    {
        super(name, Material.ROCK);

        this.VARIANTS = variants;

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, defaultValue).withProperty(RICHNESS,
                Richness.NORMAL));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
    {
        return this.getOreFromState(world.getBlockState(pos)).getResistance() / 5.0F;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos)
    {
        return this.getOreFromState(state).getHardness();
    }

    @Override
    public int getHarvestLevel(IBlockState state)
    {
        return this.getOreFromState(state).getToolLevel();
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
        for (int i = 0; i < VARIANTS.getAllowedValues().size(); i++)
            items.add(new ItemStack(this, 1, i));
    }

    public QBarOre getOreFromState(IBlockState state)
    {
        return QBarOres.getOreFromName(state.getValue(VARIANTS)).get();
    }

    public IBlockState getStateFromOre(String oreName)
    {
        return this.getDefaultState().withProperty(VARIANTS, oreName);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(VARIANTS, VARIANTS.getByIndex(meta / Richness.values().length))
                .withProperty(RICHNESS, Richness.values()[meta % Richness.values().length]);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return VARIANTS.indexOf(state.getValue(VARIANTS)) * (state.getValue(RICHNESS).ordinal() + 1);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FAKE_VARIANTS, RICHNESS);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getItemModelCount()
    {
        return this.getVARIANTS().getAllowedValues().size() * Richness.values().length;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemModelFromMeta(int itemMeta)
    {
        IBlockState state = this.getStateFromMeta(itemMeta);
        return "ores=" + state.getValue(VARIANTS) + ",richness=" + state.getValue(RICHNESS).getName();
    }

    public static class Builder
    {
        private List<QBarOre> contents = new ArrayList<>();
        private String        name;
        private String        defaultValue;

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

            return new BlockVeinOre(name, defaultValue, variants);
        }
    }

    public enum Richness implements IStringSerializable
    {
        POOR, NORMAL, RICH;

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }
}
