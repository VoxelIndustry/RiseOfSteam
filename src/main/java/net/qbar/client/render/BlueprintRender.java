package net.qbar.client.render;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

public class BlueprintRender implements IBakedModel
{

    private final IBakedModel originalModel;

    public BlueprintRender(final IBakedModel originalModel)
    {
        this.originalModel = Preconditions.checkNotNull(originalModel);
    }

    private final ItemOverrideList itemHandler = new ItemOverrideList(ImmutableList.of())
    {
        @Nonnull
        @Override
        public IBakedModel handleItemState(final IBakedModel model, final ItemStack stack, final World world,
                final EntityLivingBase entity)
        {
            final IBakedModel multiblock = BlueprintRender.this.getModel(stack);
            if (multiblock != null)
                return BlueprintRender.this.getModel(multiblock);
            return BlueprintRender.this;
        }
    };

    private IBakedModel getModel(final ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("blueprint")
                && Block.getBlockFromName("qbar:" + stack.getTagCompound().getString("blueprint")) != null)
        {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(
                    Block.getBlockFromName("qbar:" + stack.getTagCompound().getString("blueprint")).getDefaultState());
        }
        return null;
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides()
    {
        return this.itemHandler;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable final IBlockState state, @Nullable final EnumFacing side, final long rand)
    {
        return this.originalModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return this.originalModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return this.originalModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return this.originalModel.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return this.originalModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return this.originalModel.getItemCameraTransforms();
    }

    private final IdentityHashMap<IBakedModel, CompositeBakedModel> cache = new IdentityHashMap<>();

    private CompositeBakedModel getModel(final IBakedModel lens)
    {
        CompositeBakedModel model = this.cache.get(lens);
        if (model == null)
        {
            model = new CompositeBakedModel(lens, this.originalModel);
            this.cache.put(lens, model);
        }
        return model;
    }

    protected static BakedQuad transform(final BakedQuad quad, final TRSRTransformation transform)
    {
        final UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM);
        final IVertexConsumer consumer = new VertexTransformer(builder)
        {
            @Override
            public void put(final int element, final float... data)
            {
                final VertexFormatElement formatElement = DefaultVertexFormats.ITEM.getElement(element);
                switch (formatElement.getUsage())
                {
                    case POSITION:
                    {
                        final float[] newData = new float[4];
                        final Vector4f vec = new Vector4f(data);
                        transform.getMatrix().transform(vec);
                        vec.get(newData);
                        this.parent.put(element, newData);
                        break;
                    }
                    default:
                    {
                        this.parent.put(element, data);
                        break;
                    }
                }
            }
        };
        quad.pipe(consumer);
        return builder.build();
    }

    private static class CompositeBakedModel implements IPerspectiveAwareModel
    {

        private final IBakedModel                      gun;
        private final List<BakedQuad>                  genQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads = new EnumMap<>(EnumFacing.class);

        CompositeBakedModel(final IBakedModel lens, final IBakedModel gun)
        {
            this.gun = gun;

            final ImmutableList.Builder<BakedQuad> genBuilder = ImmutableList.builder();
            final TRSRTransformation transform = TRSRTransformation.blockCenterToCorner(
                    new TRSRTransformation(new Vector3f(0F, 0F, 0.3f), null, new Vector3f(0.625F, 0.625F, 0.625F),
                            TRSRTransformation.quatFromXYZ((float) Math.PI / 2, (float) Math.PI / 2, 0)));

            for (final EnumFacing e : EnumFacing.VALUES)
                this.faceQuads.put(e, new ArrayList<>());

            // Add lens quads, scaled and translated
            for (final BakedQuad quad : lens.getQuads(null, null, 0))
            {
                genBuilder.add(BlueprintRender.transform(quad, transform));
            }

            for (final EnumFacing e : EnumFacing.VALUES)
            {
                this.faceQuads.get(e).addAll(lens.getQuads(null, e, 0).stream()
                        .map(input -> BlueprintRender.transform(input, transform)).collect(Collectors.toList()));
            }

            // Add gun quads
            genBuilder.addAll(gun.getQuads(null, null, 0));
            for (final EnumFacing e : EnumFacing.VALUES)
            {
                this.faceQuads.get(e).addAll(gun.getQuads(null, e, 0));
            }

            this.genQuads = genBuilder.build();

        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(final IBlockState state, final EnumFacing face, final long rand)
        {
            return face == null ? this.genQuads : this.faceQuads.get(face);
        }

        @Override
        public boolean isAmbientOcclusion()
        {
            return this.gun.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return this.gun.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return this.gun.isBuiltInRenderer();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return this.gun.getParticleTexture();
        }

        @Nonnull
        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return this.gun.getItemCameraTransforms();
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides()
        {
            return ItemOverrideList.NONE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
                final ItemCameraTransforms.TransformType cameraTransformType)
        {
            if (cameraTransformType.equals(TransformType.GUI))
            {
                return Pair.of(this,
                        new TRSRTransformation(new Vector3f(0F, 0, 0F), null,
                                new Vector3f(0.625F * 1.5f, 0.625F * 1.5f, 0.625F * 1.5f),
                                TRSRTransformation.quatFromXYZ(0, (float) Math.PI, 0)).getMatrix());
            }
            return Pair.of(this,
                    new TRSRTransformation(new Vector3f(0F, -.25F, 0F), null,
                            new Vector3f(0.625F / 2, 0.625F / 2, 0.625F / 2),
                            TRSRTransformation.quatFromXYZ((float) -Math.PI / 2, 0, 0)).getMatrix());
        }
    }

}