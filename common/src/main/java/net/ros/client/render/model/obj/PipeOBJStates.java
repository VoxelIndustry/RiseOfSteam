package net.ros.client.render.model.obj;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.ros.common.block.property.BeltDirection;
import net.ros.common.block.property.BeltProperties;
import net.ros.common.init.ROSBlocks;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PipeOBJStates
{
    private static final HashMap<String, ROSOBJState> variants = new HashMap<>();

    private static BlockRendererDispatcher blockRender;

    public static LoadingCache<ROSOBJState, List<BakedQuad>> steamPipeCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<ROSOBJState, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(ROSOBJState key)
                {
                    return getBlockRender().getModelForState(ROSBlocks.STEAM_PIPE.getDefaultState())
                            .getQuads(((IExtendedBlockState) ROSBlocks.STEAM_PIPE.getBlockState().getBaseState())
                                    .withProperty(StateProperties.VISIBILITY_PROPERTY, key), null, 0);
                }
            });

    public static LoadingCache<ROSOBJState, List<BakedQuad>> fluidPipeCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<ROSOBJState, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(ROSOBJState key)
                {
                    return getBlockRender().getModelForState(ROSBlocks.FLUID_PIPE.getDefaultState())
                            .getQuads(((IExtendedBlockState) ROSBlocks.FLUID_PIPE.getBlockState().getBaseState())
                                    .withProperty(StateProperties.VISIBILITY_PROPERTY, key), null, 0);
                }
            });

    public static LoadingCache<EnumFacing, List<BakedQuad>> beltCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<EnumFacing, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(EnumFacing key)
                {
                    IBlockState beltState = ROSBlocks.BELT.getDefaultState().withProperty(BeltProperties.FACING,
                            BeltDirection.fromFacing(key));

                    return getBlockRender().getModelForState(beltState).getQuads(beltState, null, 0);
                }
            });

    public static BlockRendererDispatcher getBlockRender()
    {
        if (blockRender == null)
            blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
        return blockRender;
    }

    public static ROSOBJState getVisibilityState(EnumFacing... facings)
    {
        return getVisibilityState(false, facings);
    }

    public static ROSOBJState getVisibilityState(boolean forceCenter, EnumFacing... facings)
    {
        String key = getVariantKey(forceCenter, facings);

        if (!variants.containsKey(key))
            variants.put(key, buildVisibilityState(forceCenter, facings));
        return variants.get(key);
    }

    public static String getVariantKey(boolean forceCenter, EnumFacing... facings)
    {
        StringBuilder rtn = new StringBuilder(13);

        if (isStraight(facings) && forceCenter)
            rtn.append("c");
        if (isConnected(EnumFacing.EAST, facings))
            rtn.append("x+");
        if (isConnected(EnumFacing.WEST, facings))
            rtn.append("x-");
        if (isConnected(EnumFacing.UP, facings))
            rtn.append("y+");
        if (isConnected(EnumFacing.DOWN, facings))
            rtn.append("y-");
        if (isConnected(EnumFacing.SOUTH, facings))
            rtn.append("z+");
        if (isConnected(EnumFacing.NORTH, facings))
            rtn.append("z-");
        return rtn.toString();
    }

    private static ROSOBJState buildVisibilityState(boolean forceCenter, EnumFacing... facings)
    {
        List<String> parts = new ArrayList<>();

        if (facings.length == 0)
        {
            parts.add("armx1");
            parts.add("armx2");
            parts.add("army1");
            parts.add("army2");
            parts.add("armz1");
            parts.add("armz2");
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");
        }
        else if (isStraight(facings))
        {
            if (!forceCenter)
                parts.add("center");
            parts.add("armx1");
            parts.add("armx2");

            parts.add("army1");
            parts.add("army2");

            parts.add("armz1");
            parts.add("armz2");

            if (isConnected(EnumFacing.WEST, facings))
            {
                parts.add("straighty");
                parts.add("straightz");
            }
            else if (isConnected(EnumFacing.NORTH, facings))
            {
                parts.add("straighty");
                parts.add("straightx");
            }
            else if (isConnected(EnumFacing.UP, facings))
            {
                parts.add("straightx");
                parts.add("straightz");
            }
        }
        else
        {
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");

            if (!isConnected(EnumFacing.UP, facings))
                parts.add("army1");
            if (!isConnected(EnumFacing.DOWN, facings))
                parts.add("army2");
            if (!isConnected(EnumFacing.NORTH, facings))
                parts.add("armz1");
            if (!isConnected(EnumFacing.SOUTH, facings))
                parts.add("armz2");
            if (!isConnected(EnumFacing.EAST, facings))
                parts.add("armx1");
            if (!isConnected(EnumFacing.WEST, facings))
                parts.add("armx2");
        }
        return new ROSOBJState(parts, false);
    }

    private static boolean isConnected(EnumFacing facing, EnumFacing... facings)
    {
        return ArrayUtils.contains(facings, facing);
    }

    private static boolean isStraight(EnumFacing... facings)
    {
        if (facings.length == 2)
            return isConnected(EnumFacing.NORTH, facings) && isConnected(EnumFacing.SOUTH, facings)
                    || isConnected(EnumFacing.WEST, facings) && isConnected(EnumFacing.EAST, facings)
                    || isConnected(EnumFacing.UP, facings) && isConnected(EnumFacing.DOWN, facings);
        return false;
    }
}
