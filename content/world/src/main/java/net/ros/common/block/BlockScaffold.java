package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.ros.common.block.property.PropertyString;
import net.ros.common.recipe.MaterialShape;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import org.apache.commons.lang3.StringUtils;

public class BlockScaffold extends BlockMetal
{
    protected static final AxisAlignedBB LADDER_AABB = new AxisAlignedBB(0.05D, 0.0D, 0.05D, 0.95D, 1.0D,
            0.95D);

    private BlockScaffold(String name, PropertyString variants)
    {
        super(name, variants);
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
            return false;

        if (player.getHeldItemMainhand().isEmpty() || Block.getBlockFromItem(player.getHeldItemMainhand().getItem()) != this)
            return false;

        IBlockState itemVariant = this.getStateFromMeta(player.getHeldItemMainhand().getItemDamage());

        if (!state.equals(itemVariant))
            return false;

        BlockPos nextEmpty = this.getNextEmpty(w, pos);

        if (nextEmpty == BlockPos.ORIGIN)
            return false;

        w.setBlockState(nextEmpty, state);

        if (!player.isCreative())
            player.getHeldItemMainhand().shrink(1);
        return true;
    }

    private BlockPos getNextEmpty(World world, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos);
        while (mutablePos.getY() < 256)
        {
            if (world.isAirBlock(mutablePos))
                return mutablePos;
            mutablePos.setY(mutablePos.getY() + 1);
        }
        return BlockPos.ORIGIN;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return LADDER_AABB;
    }

    @Override
    public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity e)
    {
        super.onEntityCollidedWithBlock(w, pos, state, e);
        if (e instanceof EntityLivingBase && !((EntityLivingBase) e).isOnLadder() && isLadder(state, w, pos,
                (EntityLivingBase) e))
        {
            float f5 = 0.15F;
            if (e.motionX < -f5)
                e.motionX = -f5;
            if (e.motionX > f5)
                e.motionX = f5;
            if (e.motionZ < -f5)
                e.motionZ = -f5;
            if (e.motionZ > f5)
                e.motionZ = f5;

            e.fallDistance = 0.0F;
            if (e.motionY < -0.15D)
                e.motionY = -0.15D;

            if (e.motionY < 0 && e instanceof EntityPlayer && e.isSneaking())
            {
                e.motionY = 0;
                return;
            }
            if (e.collidedHorizontally)
                e.motionY = .2;
        }
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
