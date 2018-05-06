package net.ros.common.block;

import lombok.Getter;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ros.common.ROSConstants;

public class BlockOreSand extends BlockFalling implements IModelProvider
{
    private static final PropertyEnum<BlockOreSand.EnumType> VARIANTS =
            PropertyEnum.create("variant", BlockOreSand.EnumType.class);

    public BlockOreSand(String name)
    {
        super(Material.SAND);
        this.setSoundType(SoundType.SAND);

        this.setRegistryName(ROSConstants.MODID, name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(ROSConstants.TAB_ALL);

        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, EnumType.COPPER_SAND));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getItemModelCount()
    {
        return VARIANTS.getAllowedValues().size();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemModelByIndex(int index)
    {
        return "variant=" + this.getStateFromMeta(index).getValue(VARIANTS);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.getValue(VARIANTS).getMapColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state)
    {
        return state.getValue(VARIANTS).getDustColor();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < EnumType.values().length; i++)
            items.add(new ItemStack(this, 1, i));
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        return this.getDefaultState().withProperty(VARIANTS, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        return state.getValue(VARIANTS).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANTS);
    }

    @Getter
    public enum EnumType implements IStringSerializable
    {
        COPPER_SAND("copper_sand", MapColor.ORANGE_STAINED_HARDENED_CLAY, -2370656),
        TIN_SAND("tin_sand", MapColor.IRON, -5679071);

        private final String   name;
        private final MapColor mapColor;
        private final int      dustColor;

        EnumType(String name, MapColor mapColor, int dustColor)
        {
            this.name = name;
            this.mapColor = mapColor;
            this.dustColor = dustColor;
        }

        @SideOnly(Side.CLIENT)
        public int getDustColor()
        {
            return this.dustColor;
        }

        public String toString()
        {
            return this.name;
        }

        public static EnumType byMetadata(int meta)
        {
            if (meta < values().length)
                return values()[meta];
            return null;
        }
    }
}
