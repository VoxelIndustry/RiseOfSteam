package net.ros.client.render;

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
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;
import net.ros.common.ROSConstants;
import net.ros.common.machine.Machines;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.blueprint.Blueprint;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.*;
import java.util.stream.Collectors;

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
            if (Machines.contains(Blueprint.class, stack.getTagCompound().getString("blueprint")))
            {
                final MultiblockComponent descriptor = Machines.getComponent(MultiblockComponent.class,
                        stack.getTagCompound().getString("blueprint"));
                if (multiblock != null)
                    return BlueprintRender.this.getModel(multiblock, descriptor);
            }
            return BlueprintRender.this;
        }
    };

    private IBakedModel getModel(final ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("blueprint")
                && Block.getBlockFromName(ROSConstants.MODID + ":"
                + stack.getTagCompound().getString("blueprint")) != null)
        {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(
                    Block.getBlockFromName(ROSConstants.MODID + ":"
                            + stack.getTagCompound().getString("blueprint")).getDefaultState());
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

    private CompositeBakedModel getModel(final IBakedModel multiblock, final MultiblockComponent descriptor)
    {
        CompositeBakedModel model = this.cache.get(multiblock);
        if (model == null)
        {
            model = new CompositeBakedModel(multiblock, this.originalModel, descriptor);
            this.cache.put(multiblock, model);
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

    private static class CompositeBakedModel implements IBakedModel
    {

        private final IBakedModel                      blueprint;
        private final List<BakedQuad>                  genQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads = new EnumMap<>(EnumFacing.class);

        CompositeBakedModel(final IBakedModel multiblock, final IBakedModel blueprint,
                            final MultiblockComponent descriptor)
        {
            this.blueprint = blueprint;

            final ImmutableList.Builder<BakedQuad> genBuilder = ImmutableList.builder();

            final float maxBlock = (float) Math.max(descriptor.getHeight() / 2.0,
                    Math.max(descriptor.getWidth(), descriptor.getLength()));
            final TRSRTransformation transform = TRSRTransformation
                    .blockCenterToCorner(new TRSRTransformation(new Vector3f(0F, 0F, 0.3f - 0.045f * maxBlock), null,
                            new Vector3f(0.625F / maxBlock, 0.625F / maxBlock, 0.625F / maxBlock),
                            TRSRTransformation.quatFromXYZ((float) Math.PI / 2, (float) Math.PI / 3, 0)));

            for (final EnumFacing e : EnumFacing.VALUES)
                this.faceQuads.put(e, new ArrayList<>());

            for (final BakedQuad quad : multiblock.getQuads(null, null, 0))
            {
                genBuilder.add(BlueprintRender.transform(quad, transform));
            }

            for (final EnumFacing e : EnumFacing.VALUES)
            {
                this.faceQuads.get(e).addAll(multiblock.getQuads(null, e, 0).stream()
                        .map(input -> BlueprintRender.transform(input, transform)).collect(Collectors.toList()));
            }

            genBuilder.addAll(blueprint.getQuads(null, null, 0));
            for (final EnumFacing e : EnumFacing.VALUES)
            {
                this.faceQuads.get(e).addAll(blueprint.getQuads(null, e, 0));
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
            return this.blueprint.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d()
        {
            return this.blueprint.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer()
        {
            return this.blueprint.isBuiltInRenderer();
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture()
        {
            return this.blueprint.getParticleTexture();
        }

        @Nonnull
        @Override
        public ItemCameraTransforms getItemCameraTransforms()
        {
            return this.blueprint.getItemCameraTransforms();
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
                    new TRSRTransformation(new Vector3f(0F, -.2F, 0F), null,
                            new Vector3f(0.625F / 2, 0.625F / 2, 0.625F / 2),
                            TRSRTransformation.quatFromXYZ((float) -Math.PI / 2, 0, 0)).getMatrix());
        }
    }

}