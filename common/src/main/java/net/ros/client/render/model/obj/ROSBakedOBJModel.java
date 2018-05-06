package net.ros.client.render.model.obj;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ROSBakedOBJModel extends OBJModel.OBJBakedModel
{
    private ImmutableList<BakedQuad> quads;

    private final ROSOBJModel  model;
    private       IModelState  state;
    private final VertexFormat format;

    private ImmutableMap<String, TextureAtlasSprite> textures;
    private TextureAtlasSprite sprite = ModelLoader.White.INSTANCE;

    private LoadingCache<ROSOBJState, ImmutableList<BakedQuad>> cachedVariants;

    public ROSBakedOBJModel(final ROSOBJModel model, final IModelState state, final VertexFormat format,
                            final ImmutableMap<String, TextureAtlasSprite> textures)
    {
        model.super(model, state, format, textures);

        this.model = model;
        this.state = state;
        this.format = format;
        this.textures = textures;

        this.cachedVariants = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(20, TimeUnit.MINUTES)
                .build(new CacheLoader<ROSOBJState, ImmutableList<BakedQuad>>()
                {
                    @Override
                    public ImmutableList<BakedQuad> load(final ROSOBJState key)
                    {
                        return ROSBakedOBJModel.this.buildQuads(new CompositeModelState(state, key));
                    }
                });
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState blockState, EnumFacing side, long rand)
    {
        if (side != null)
            return ImmutableList.of();
        if (quads == null)
            quads = buildQuads(this.state);
        if (blockState instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState) blockState;
            if (exState.getUnlistedNames().contains(StateProperties.VISIBILITY_PROPERTY))
            {
                ROSOBJState newState = exState.getValue(StateProperties.VISIBILITY_PROPERTY);
                if (newState != null)
                {
                    try
                    {
                        return this.cachedVariants.get(newState);
                    } catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return quads;
    }

    private ImmutableList<BakedQuad> buildQuads(IModelState modelState)
    {
        List<BakedQuad> quads = Lists.newArrayList();
        Collections.synchronizedSet(new LinkedHashSet<BakedQuad>());
        Set<OBJModel.Face> faces = Collections.synchronizedSet(new LinkedHashSet<OBJModel.Face>());
        Optional<TRSRTransformation> transform = Optional.empty();

        for (OBJModel.Group g : this.model.getMatLib().getGroups().values())
        {
            if (modelState.apply(Optional.of(Models.getHiddenModelPart(ImmutableList.of(g.getName())))).isPresent())
            {
                continue;
            }
            if (modelState instanceof ROSOBJState)
            {
                ROSOBJState state = (ROSOBJState) modelState;
                if (state.parent != null)
                    transform = state.parent.apply(Optional.empty());

                if (state.isWhitelist() && state.getVisibilityList().contains(g.getName()))
                    faces.addAll(g.applyTransform(transform));
                else if (!state.isWhitelist() && !state.getVisibilityList().contains(g.getName()))
                    faces.addAll(g.applyTransform(transform));
            }
            else if (modelState instanceof CompositeModelState)
            {
                ROSOBJState state = (ROSOBJState) ((CompositeModelState) modelState).getSecond();
                transform = modelState.apply(Optional.empty());

                if (state.isWhitelist() && state.getVisibilityList().contains(g.getName()))
                    faces.addAll(g.applyTransform(transform));
                else if (!state.isWhitelist() && !state.getVisibilityList().contains(g.getName()))
                    faces.addAll(g.applyTransform(transform));
            }
            else
            {
                transform = state.apply(Optional.empty());

                faces.addAll(g.applyTransform(transform));
            }
        }
        for (OBJModel.Face f : faces)
        {
            if (this.model.getMatLib().getMaterial(f.getMaterialName()).isWhite())
            {
                for (OBJModel.Vertex v : f.getVertices())
                {
                    if (!v.getMaterial().equals(this.model.getMatLib().getMaterial(v.getMaterial().getName())))
                        v.setMaterial(this.model.getMatLib().getMaterial(v.getMaterial().getName()));
                }
                sprite = ModelLoader.White.INSTANCE;
            }
            else
                sprite = this.textures.get(f.getMaterialName());
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
            builder.setContractUVs(true);
            builder.setQuadOrientation(
                    EnumFacing.getFacingFromVector(f.getNormal().x, f.getNormal().y, f.getNormal().z));
            builder.setTexture(sprite);
            OBJModel.Normal faceNormal = f.getNormal();
            putVertexData(builder, f.getVertices()[0], faceNormal, OBJModel.TextureCoordinate.getDefaultUVs()[0],
                    sprite);
            putVertexData(builder, f.getVertices()[1], faceNormal, OBJModel.TextureCoordinate.getDefaultUVs()[1],
                    sprite);
            putVertexData(builder, f.getVertices()[2], faceNormal, OBJModel.TextureCoordinate.getDefaultUVs()[2],
                    sprite);
            putVertexData(builder, f.getVertices()[3], faceNormal, OBJModel.TextureCoordinate.getDefaultUVs()[3],
                    sprite);
            quads.add(builder.build());
        }
        return ImmutableList.copyOf(quads);
    }

    private final void putVertexData(UnpackedBakedQuad.Builder builder, OBJModel.Vertex v, OBJModel.Normal faceNormal,
                                     OBJModel.TextureCoordinate defUV, TextureAtlasSprite sprite)
    {
        for (int e = 0; e < format.getElementCount(); e++)
        {
            switch (format.getElement(e).getUsage())
            {
                case POSITION:
                    builder.put(e, v.getPos().x, v.getPos().y, v.getPos().z, v.getPos().w);
                    break;
                case COLOR:
                    if (v.getMaterial() != null)
                        builder.put(e, v.getMaterial().getColor().x, v.getMaterial().getColor().y,
                                v.getMaterial().getColor().z, v.getMaterial().getColor().w);
                    else
                        builder.put(e, 1, 1, 1, 1);
                    break;
                case UV:
                    if (!v.hasTextureCoordinate())
                        builder.put(e, sprite.getInterpolatedU(defUV.u * 16),
                                sprite.getInterpolatedV((1 - defUV.v) * 16), 0, 1);
                    else
                        builder.put(e, sprite.getInterpolatedU(v.getTextureCoordinate().u * 16),
                                sprite.getInterpolatedV((1 - v.getTextureCoordinate().v) * 16), 0, 1);
                    break;
                case NORMAL:
                    if (!v.hasNormal())
                        builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    else
                        builder.put(e, v.getNormal().x, v.getNormal().y, v.getNormal().z, 0);
                    break;
                default:
                    builder.put(e);
            }
        }
    }
}
