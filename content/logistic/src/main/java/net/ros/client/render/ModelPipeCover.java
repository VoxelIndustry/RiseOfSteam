package net.ros.client.render;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.ros.client.render.model.ModelCacheManager;
import net.ros.client.render.model.obj.PipeOBJStates;
import net.ros.client.render.model.obj.ROSOBJState;
import net.ros.client.render.model.obj.StateProperties;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModelPipeCover implements IBakedModel
{
    private final Table<ROSOBJState, EnumFacing, CompositeBakedModel> CACHE = HashBasedTable.create();

    private final ResourceLocation modelLocation;
    private final Block            block;

    public ModelPipeCover(ResourceLocation modelLocation, Block block)
    {
        this.modelLocation = modelLocation;
        this.block = block;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long rand)
    {
        return getModel(state, (ROSOBJState) ((IExtendedBlockState) state).getUnlistedProperties()
                        .get(StateProperties.VISIBILITY_PROPERTY).get(),
                state.getValue(BlockDirectional.FACING)).getQuads(state, face, rand);
    }

    private CompositeBakedModel getModel(IBlockState coverState, ROSOBJState pipeState, EnumFacing coverFacing)
    {
        ModelManager modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();

        if (CACHE.contains(pipeState, coverFacing))
        {
            return CACHE.get(pipeState, coverFacing);
        }
        else
        {
            IBakedModel coverModel;
            try
            {
                coverModel = ModelLoaderRegistry.getModel(modelLocation)
                        .process(ImmutableMap.of("flip-v", "true"))
                        .bake(TRSRTransformation.from(coverState.getValue(BlockDirectional.FACING).getOpposite()),
                                DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

            } catch (Exception e)
            {
                coverModel = modelManager.getMissingModel();
            }

            CompositeBakedModel model = new CompositeBakedModel(coverState,
                    ModelCacheManager.getPipeQuads(block, pipeState), coverModel);
            CACHE.put(pipeState, coverFacing, model);
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
        private       IBakedModel                      coverModel;
        private final List<BakedQuad>                  genQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads = new EnumMap<>(EnumFacing.class);

        CompositeBakedModel(IBlockState coverState, List<BakedQuad> pipeQuads, IBakedModel coverModel)
        {
            this.coverModel = coverModel;

            ImmutableList.Builder<BakedQuad> genBuilder = ImmutableList.builder();

            for (EnumFacing e : EnumFacing.VALUES)
                faceQuads.put(e, new ArrayList<>());

            coverModel.getQuads(coverState, null, 0).forEach(genBuilder::add);
            for (EnumFacing e : EnumFacing.VALUES)
                coverModel.getQuads(coverState, e, 0).forEach(faceQuads.get(e)::add);

            genBuilder.addAll(pipeQuads);

            genQuads = genBuilder.build();
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long rand)
        {
            return face == null ? genQuads : faceQuads.get(face);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return coverModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return coverModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return coverModel.isBuiltInRenderer();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return coverModel.getParticleTexture();
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
            return Pair.of(this, TRSRTransformation.from(coverModel.getItemCameraTransforms()
                    .getTransform(cameraTransformType)).getMatrix());
        }
    }

    private final ItemOverrideList itemHandler = new ItemOverrideList(ImmutableList.of())
    {
        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel model, ItemStack stack, World world, EntityLivingBase
                entity)
        {
            return ModelPipeCover.this.getModel(block.getDefaultState(),
                    PipeOBJStates.getVisibilityState(EnumFacing.UP, EnumFacing.DOWN), EnumFacing.NORTH);
        }
    };
}
