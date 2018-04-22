package net.qbar.client.render;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.qbar.client.render.model.obj.PipeOBJStates;
import net.qbar.client.render.model.obj.QBarOBJState;
import net.qbar.client.render.model.obj.QBarStateProperties;
import net.qbar.common.QBarConstants;
import net.qbar.common.block.property.BeltDirection;
import net.qbar.common.block.property.BeltProperties;
import net.qbar.common.init.QBarBlocks;
import net.qbar.common.machine.FluidIOPoint;
import net.qbar.common.machine.InputPoint;
import net.qbar.common.machine.OutputPoint;
import net.qbar.common.machine.component.AutomationComponent;
import net.qbar.common.machine.component.IOComponent;
import net.qbar.common.machine.module.impl.IOModule;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.tile.machine.TileModularMachine;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RenderIOOverlay
{
    public static BlockRendererDispatcher blockRender;

    private static LoadingCache<QBarOBJState, List<BakedQuad>> steamPipeCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<QBarOBJState, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(QBarOBJState key)
                {
                    return blockRender.getModelForState(QBarBlocks.STEAM_PIPE.getDefaultState())
                            .getQuads(((IExtendedBlockState) QBarBlocks.STEAM_PIPE.getBlockState().getBaseState())
                                    .withProperty(QBarStateProperties.VISIBILITY_PROPERTY, key), null, 0);
                }
            });

    private static LoadingCache<QBarOBJState, List<BakedQuad>> fluidPipeCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<QBarOBJState, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(QBarOBJState key)
                {
                    return blockRender.getModelForState(QBarBlocks.FLUID_PIPE.getDefaultState())
                            .getQuads(((IExtendedBlockState) QBarBlocks.FLUID_PIPE.getBlockState().getBaseState())
                                    .withProperty(QBarStateProperties.VISIBILITY_PROPERTY, key), null, 0);
                }
            });

    private static LoadingCache<EnumFacing, List<BakedQuad>> beltCache = CacheBuilder.newBuilder()
            .weakKeys().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<EnumFacing, List<BakedQuad>>()
            {
                @Override
                public List<BakedQuad> load(EnumFacing key)
                {
                    IBlockState beltState = QBarBlocks.BELT.getDefaultState().withProperty(BeltProperties.FACING,
                            BeltDirection.fromFacing(key));

                    return blockRender.getModelForState(beltState).getQuads(beltState, null, 0);
                }
            });

    public static void renderIO(EntityPlayerSP player, double playerX, double playerY, double playerZ,
                                float partialTicks) throws ExecutionException
    {
        if (blockRender == null)
            blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

        if (player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(QBarBlocks.STEAM_PIPE)
                && player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(QBarBlocks.FLUID_PIPE)
                && player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(QBarBlocks.BELT))
            return;

        List<TileModularMachine> tiles = new ArrayList<>();

        BlockPos.getAllInBoxMutable(player.getPosition().add(-8, -4, -8), player.getPosition().add(8, 4, 8))
                .forEach(pos ->
                {
                    TileEntity tile = player.getEntityWorld().getTileEntity(pos);

                    if (tile instanceof TileMultiblockGag &&
                            ((TileMultiblockGag) tile).getCore() instanceof TileModularMachine &&
                            !tiles.contains(((TileMultiblockGag) tile).getCore()))
                    {
                        if (((TileModularMachine) ((TileMultiblockGag) tile).getCore()).hasModule(IOModule.class) &&
                                ((TileModularMachine) ((TileMultiblockGag) tile).getCore()).getDescriptor() != null)
                            tiles.add((TileModularMachine) ((TileMultiblockGag) tile).getCore());
                    }
                    else if (tile instanceof TileModularMachine && tiles.contains(tile))
                    {
                        if (((TileModularMachine) tile).hasModule(IOModule.class) &&
                                ((TileModularMachine) tile).getDescriptor() != null)
                            tiles.add((TileModularMachine) tile);
                    }
                });

        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-playerX, -playerY, -playerZ);

        final Minecraft minecraft = Minecraft.getMinecraft();

        minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.colorMask(true, true, true, true);

        for (TileModularMachine tile : tiles)
        {
            if (tile.getDescriptor().has(IOComponent.class))
            {
                IOComponent io = tile.getDescriptor().get(IOComponent.class);

                if (player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(QBarBlocks.STEAM_PIPE))
                {
                    for (MultiblockSide point : io.getSteamIO())
                    {
                        MultiblockSide side = tile.getDescriptor().get(MultiblockComponent.class)
                                .multiblockSideToWorldSide(point, tile.getFacing());

                        BlockPos offset = side.getPos().offset(side.getFacing()).add(tile.getPos());

                        if (!player.getEntityWorld().isAirBlock(offset))
                            continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                        renderSteam(player.getEntityWorld(), offset, side);
                        GlStateManager.popMatrix();
                    }
                }
                else if (player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(QBarBlocks.FLUID_PIPE))
                {
                    for (FluidIOPoint point : io.getFluidIO())
                    {
                        MultiblockSide side = tile.getDescriptor().get(MultiblockComponent.class)
                                .multiblockSideToWorldSide(point.getSide(), tile.getFacing());

                        BlockPos offset = side.getPos().offset(side.getFacing()).add(tile.getPos());

                        if (!player.getEntityWorld().getBlockState(offset).getBlock()
                                .isReplaceable(player.getEntityWorld(), offset))
                            continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                        renderFluid(player.getEntityWorld(), offset, side, point);
                        GlStateManager.popMatrix();
                    }
                }
                else if (tile.getDescriptor().has(AutomationComponent.class))
                {
                    AutomationComponent automation = tile.getDescriptor().get(AutomationComponent.class);

                    for (InputPoint point : automation.getInputs())
                    {
                        MultiblockSide side = tile.getDescriptor().get(MultiblockComponent.class)
                                .multiblockSideToWorldSide(point.getSide(), tile.getFacing());

                        BlockPos offset = side.getPos().offset(side.getFacing()).add(tile.getPos()).down();

                        if (!player.getEntityWorld().isAirBlock(offset))
                            continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                        renderBelt(player.getEntityWorld(), offset, point.getConnText(),
                                side.getFacing().getOpposite());
                        GlStateManager.popMatrix();
                    }
                    for (OutputPoint point : automation.getOutputs())
                    {
                        MultiblockSide side = tile.getDescriptor().get(MultiblockComponent.class)
                                .multiblockSideToWorldSide(point.getSide(), tile.getFacing());

                        BlockPos offset = side.getPos().add(tile.getPos());

                        if (!player.getEntityWorld().isAirBlock(offset))
                            continue;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                        renderBelt(player.getEntityWorld(), offset, point.getConnText(),
                                side.getFacing().getOpposite());
                        GlStateManager.popMatrix();
                    }
                }
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GL11.glEnable(GL11.GL_LIGHTING);

        GlStateManager.popMatrix();
    }

    private static void renderSteam(World w, BlockPos pos, MultiblockSide side)
            throws ExecutionException
    {
        List<EnumFacing> facings = new ArrayList<>(6);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == side.getFacing().getOpposite())
                facings.add(facing);
            if (w.getBlockState(pos.offset(facing)).getBlock() == QBarBlocks.STEAM_PIPE)
                facings.add(facing);
        }

        RenderUtil.renderQuads(steamPipeCache.get(PipeOBJStates.getVisibilityState(facings.toArray(new EnumFacing[0]))),
                (int) (0.6 * 0xFF) << 24);
    }

    private static final ResourceLocation TEX_IO = new ResourceLocation(QBarConstants.MODID,
            "textures/effects/overlay_input_output.png");
    private static final ResourceLocation TEX_O  = new ResourceLocation(QBarConstants.MODID,
            "textures/effects/overlay_input.png");
    private static final ResourceLocation TEX_I  = new ResourceLocation(QBarConstants.MODID,
            "textures/effects/overlay_output.png");


    private static void renderFluid(World w, BlockPos pos, MultiblockSide side, FluidIOPoint point)
            throws ExecutionException
    {
        List<EnumFacing> facings = new ArrayList<>(6);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == side.getFacing().getOpposite())
                facings.add(facing);
            if (w.getBlockState(pos.offset(facing)).getBlock() == QBarBlocks.FLUID_PIPE)
                facings.add(facing);
        }

        RenderUtil.renderTexOnSide(0.5, 0.55, 0.5, 0.3, 0.3, -0.5,
                Minecraft.getMinecraft().player.getHorizontalFacing().getOpposite(),
                point.isInput() && point.isOutput() ? TEX_IO : (point.isInput() ? TEX_I : TEX_O));

        if (Minecraft.getMinecraft().player.getPosition().distanceSq(pos) < 12)
            RenderUtil.renderTextOnSide(0.5, 0.45, 0.5, 0.012, -0.5,
                    Minecraft.getMinecraft().player.getHorizontalFacing().getOpposite(),
                    I18n.format("conn." + point.getTankName() + ".name"), Color.LIGHT_GRAY);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        RenderUtil.renderQuads(fluidPipeCache.get(PipeOBJStates.getVisibilityState(facings.toArray(new EnumFacing[0]))),
                (int) (0.6 * 0xFF) << 24);
    }

    private static void renderBelt(World w, BlockPos pos, String connText, EnumFacing facing) throws ExecutionException
    {
        if (!StringUtils.isEmpty(connText) && Minecraft.getMinecraft().player.getPosition().distanceSq(pos) < 12)
            RenderUtil.renderTextOnSide(0.5, 1.25, 0.5, 0.012, 0,
                    Minecraft.getMinecraft().player.getHorizontalFacing().getOpposite(),
                    I18n.format("conn." + connText + ".name"), Color.LIGHT_GRAY);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        RenderUtil.renderQuads(beltCache.get(facing), (int) (0.6 * 0xFF) << 24);
    }
}
