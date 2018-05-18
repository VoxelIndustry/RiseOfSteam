package net.ros.client.render;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
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
import net.ros.client.render.model.obj.PipeOBJStates;
import net.ros.client.render.model.obj.ROSOBJState;
import net.ros.client.render.model.obj.StateProperties;
import net.ros.common.ROSConstants;
import net.ros.common.block.BlockSteamValve;
import net.ros.common.init.ROSBlocks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ModelSteamValve implements IBakedModel
{
    private final Table<ROSOBJState, EnumFacing, CompositeBakedModel> CACHE = HashBasedTable.create();

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing face, long rand)
    {
        return getModel(state, (ROSOBJState) ((IExtendedBlockState) state).getUnlistedProperties()
                        .get(StateProperties.VISIBILITY_PROPERTY).get(),
                state.getValue(BlockSteamValve.FACING)).getQuads(state, face, rand);
    }

    private CompositeBakedModel getModel(IBlockState valveState, ROSOBJState pipeState, EnumFacing valveFacing)
    {
        ModelManager modelManager = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager();

        if (CACHE.contains(pipeState, valveFacing))
        {
            return CACHE.get(pipeState, valveFacing);
        }
        else
        {
            IBakedModel valveModel;
            try
            {
                valveModel = ModelLoaderRegistry.getModel(
                        new ResourceLocation(ROSConstants.MODID, "block/steamvalve.obj"))
                        .process(ImmutableMap.of("flip-v", "true"))
                        .bake(TRSRTransformation.from(valveState.getValue(BlockSteamValve.FACING).getOpposite()),
                                DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());

            } catch (Exception e)
            {
                valveModel = modelManager.getMissingModel();
            }

            CompositeBakedModel model = null;
            try
            {
                model = new CompositeBakedModel(valveState, PipeOBJStates.steamPipeCache.get(pipeState), valveModel);
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            CACHE.put(pipeState, valveFacing, model);
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
        private       IBakedModel                      valveModel;
        private final List<BakedQuad>                  genQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads = new EnumMap<>(EnumFacing.class);

        public CompositeBakedModel(IBlockState valveState, List<BakedQuad> pipeQuads, IBakedModel valveModel)
        {
            this.valveModel = valveModel;

            ImmutableList.Builder<BakedQuad> genBuilder = ImmutableList.builder();

            for (EnumFacing e : EnumFacing.VALUES)
                faceQuads.put(e, new ArrayList<>());

            valveModel.getQuads(valveState, null, 0).forEach(genBuilder::add);
            for (EnumFacing e : EnumFacing.VALUES)
                valveModel.getQuads(valveState, e, 0).forEach(faceQuads.get(e)::add);

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
            return valveModel.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return valveModel.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return valveModel.isBuiltInRenderer();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return valveModel.getParticleTexture();
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
            return Pair.of(this, TRSRTransformation.from(valveModel.getItemCameraTransforms()
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
            return ModelSteamValve.this.getModel(ROSBlocks.STEAM_VALVE.getDefaultState(),
                    PipeOBJStates.getVisibilityState(EnumFacing.UP, EnumFacing.DOWN), EnumFacing.NORTH);
        }
    };
}
