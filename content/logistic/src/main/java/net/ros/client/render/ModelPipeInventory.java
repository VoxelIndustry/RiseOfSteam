package net.ros.client.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.ros.client.render.model.ModelCacheManager;
import net.ros.client.render.model.obj.PipeOBJStates;
import net.ros.client.render.model.obj.ROSOBJState;
import net.ros.common.block.BlockPipeBase;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelPipeInventory implements IBakedModel
{
    private final Map<ROSOBJState, CompositeBakedModel> CACHE = new HashMap<>();

    private final BlockPipeBase pipeBlock;

    public ModelPipeInventory(BlockPipeBase pipeBlock)
    {
        this.pipeBlock = pipeBlock;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long rand)
    {
        return Collections.emptyList();
    }

    private CompositeBakedModel getModel(ROSOBJState pipeState)
    {
        if (CACHE.containsKey(pipeState))
            return CACHE.get(pipeState);
        else
        {
            CompositeBakedModel model = new CompositeBakedModel(ModelCacheManager.getPipeQuads(pipeBlock, pipeState),
                    Minecraft.getMinecraft().getBlockRendererDispatcher()
                            .getModelForState(pipeBlock.getDefaultState()));
            CACHE.put(pipeState, model);
            return model;
        }
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides()
    {
        return itemHandler;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/dirt");
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    private static class CompositeBakedModel implements IBakedModel
    {
        private       IBakedModel     pipeModel;
        private final List<BakedQuad> genQuads;

        CompositeBakedModel(List<BakedQuad> pipeQuads, IBakedModel pipeModel)
        {
            this.pipeModel = pipeModel;

            ImmutableList.Builder<BakedQuad> genBuilder = ImmutableList.builder();
            genBuilder.addAll(pipeQuads);

            genQuads = genBuilder.build();
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long rand)
        {
            return face == null ? genQuads : Collections.emptyList();
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return pipeModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return pipeModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return pipeModel.isBuiltInRenderer();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return pipeModel.getParticleTexture();
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType
                                                                               cameraTransformType)
        {
            return Pair.of(this, pipeModel.handlePerspective(cameraTransformType).getRight());
        }
    }

    private final ItemOverrideList itemHandler = new ItemOverrideList(ImmutableList.of())
    {
        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel model, ItemStack stack, World world,
                                           EntityLivingBase entity)
        {
            return ModelPipeInventory.this.getModel(PipeOBJStates.getVisibilityState(
                    pipeBlock.getPipeType().getSize(), EnumFacing.WEST, EnumFacing.EAST));
        }
    };
}
