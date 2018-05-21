package net.ros.client.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.ros.client.render.model.obj.ROSOBJState;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.block.property.BeltDirection;
import net.ros.common.block.property.BeltProperties;
import net.ros.common.init.ROSBlocks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ModelCacheManager
{
    private static LoadingCache<Pair<Block, ROSOBJState>, List<BakedQuad>> pipeCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Pair<Block, ROSOBJState>, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(@Nonnull Pair<Block, ROSOBJState> key)
                {
                    return getBlockRender().getModelForState(key.getLeft().getDefaultState())
                            .getQuads(((IExtendedBlockState) key.getLeft().getBlockState().getBaseState())
                                    .withProperty(StateProperties.VISIBILITY_PROPERTY, key.getRight()), null, 0);
                }
            });

    private static LoadingCache<EnumFacing, List<BakedQuad>> beltCache = CacheBuilder.newBuilder()
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

    private static BlockRendererDispatcher blockRender;

    private static BlockRendererDispatcher getBlockRender()
    {
        if (blockRender == null)
            blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
        return blockRender;
    }

    public static List<BakedQuad> getBeltQuads(EnumFacing facing)
    {
        try
        {
            return beltCache.get(facing);
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static List<BakedQuad> getPipeQuads(Block pipeBlock, ROSOBJState pipeState)
    {
        try
        {
            return pipeCache.get(Pair.of(pipeBlock, pipeState));
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
