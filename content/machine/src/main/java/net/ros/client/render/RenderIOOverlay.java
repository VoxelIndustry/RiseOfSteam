package net.ros.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.ros.client.render.model.obj.PipeOBJStates;
import net.ros.common.ROSConstants;
import net.ros.common.init.ROSBlocks;
import net.ros.common.machine.FluidIOPoint;
import net.ros.common.machine.InputPoint;
import net.ros.common.machine.MachineDescriptor;
import net.ros.common.machine.OutputPoint;
import net.ros.common.machine.component.AutomationComponent;
import net.ros.common.machine.component.IOComponent;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.multiblock.MultiblockComponent;
import net.ros.common.multiblock.MultiblockSide;
import net.ros.common.multiblock.TileMultiblockGag;
import net.ros.common.multiblock.blueprint.Blueprint;
import net.ros.common.tile.TileStructure;
import net.ros.common.tile.machine.TileModularMachine;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.yggard.brokkgui.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RenderIOOverlay
{
    public static void renderIO(EntityPlayerSP player, double playerX, double playerY, double playerZ,
                                float partialTicks) throws ExecutionException
    {
        if (player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(ROSBlocks.STEAM_PIPE)
                && player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(ROSBlocks.FLUID_PIPE)
                && player.getHeldItemMainhand().getItem() != Item.getItemFromBlock(ROSBlocks.BELT))
            return;

        List<TileModularMachine> tiles = new ArrayList<>();
        List<TileStructure> structureTiles = new ArrayList<>();

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
                    else if (tile instanceof TileModularMachine && !tiles.contains(tile))
                    {
                        if (((TileModularMachine) tile).hasModule(IOModule.class) &&
                                ((TileModularMachine) tile).getDescriptor() != null)
                            tiles.add((TileModularMachine) tile);
                    }
                    else if (tile instanceof TileMultiblockGag &&
                            ((TileMultiblockGag) tile).getCore() instanceof TileStructure
                            && !structureTiles.contains(((TileMultiblockGag) tile).getCore()))
                    {
                        Blueprint blueprint = ((TileStructure) ((TileMultiblockGag) tile).getCore()).getBlueprint();
                        if (blueprint != null && blueprint.getDescriptor() != null &&
                                (blueprint.getDescriptor().has(IOComponent.class) ||
                                        blueprint.getDescriptor().has(AutomationComponent.class)))
                            structureTiles.add((TileStructure) ((TileMultiblockGag) tile).getCore());
                    }
                    else if (tile instanceof TileStructure && !structureTiles.contains(tile))
                    {
                        Blueprint blueprint = ((TileStructure) tile).getBlueprint();
                        if (blueprint != null && blueprint.getDescriptor() != null &&
                                (blueprint.getDescriptor().has(IOComponent.class) ||
                                        blueprint.getDescriptor().has(AutomationComponent.class)))
                            structureTiles.add((TileStructure) tile);
                    }
                });

        GlStateManager.pushAttrib();
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
            renderTile(tile.getDescriptor(), tile.getFacing(), tile.getPos(), player);
        for (TileStructure structure : structureTiles)
            renderTile(structure.getBlueprint().getDescriptor(), EnumFacing.VALUES[structure.getMeta()],
                    structure.getPos(), player);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private static void renderTile(MachineDescriptor descriptor, EnumFacing tileFacing, BlockPos tilePos,
                                   EntityPlayer player) throws ExecutionException
    {
        if (descriptor.has(IOComponent.class))
        {
            IOComponent io = descriptor.get(IOComponent.class);

            if (player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(ROSBlocks.STEAM_PIPE))
            {
                for (MultiblockSide point : io.getSteamIO())
                {
                    MultiblockSide side = descriptor.get(MultiblockComponent.class)
                            .multiblockSideToWorldSide(point, tileFacing);

                    BlockPos offset = side.getPos().offset(side.getFacing()).add(tilePos);

                    if (!player.getEntityWorld().isAirBlock(offset))
                        continue;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                    renderSteam(player.getEntityWorld(), offset, side);
                    GlStateManager.popMatrix();
                }
            }
            else if (player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(ROSBlocks.FLUID_PIPE))
            {
                for (FluidIOPoint point : io.getFluidIO())
                {
                    MultiblockSide side = descriptor.get(MultiblockComponent.class)
                            .multiblockSideToWorldSide(point.getSide(), tileFacing);

                    BlockPos offset = side.getPos().offset(side.getFacing()).add(tilePos);

                    if (!player.getEntityWorld().getBlockState(offset).getBlock()
                            .isReplaceable(player.getEntityWorld(), offset))
                        continue;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());

                    renderFluid(player.getEntityWorld(), offset, side, point);
                    GlStateManager.popMatrix();
                }
            }
            else if (descriptor.has(AutomationComponent.class))
            {
                AutomationComponent automation = descriptor.get(AutomationComponent.class);

                for (InputPoint point : automation.getInputs())
                {
                    MultiblockSide side = descriptor.get(MultiblockComponent.class)
                            .multiblockSideToWorldSide(point.getSide(), tileFacing);

                    BlockPos offset = side.getPos().offset(side.getFacing()).add(tilePos).down();

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
                    MultiblockSide side = descriptor.get(MultiblockComponent.class)
                            .multiblockSideToWorldSide(point.getSide(), tileFacing);

                    BlockPos offset = side.getPos().add(tilePos);

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

    private static void renderSteam(World w, BlockPos pos, MultiblockSide side)
            throws ExecutionException
    {
        List<EnumFacing> facings = new ArrayList<>(6);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == side.getFacing().getOpposite())
                facings.add(facing);
            if (w.getBlockState(pos.offset(facing)).getBlock() == ROSBlocks.STEAM_PIPE)
                facings.add(facing);
        }

        RenderUtil.renderQuads(PipeOBJStates.steamPipeCache.get(
                PipeOBJStates.getVisibilityState(facings.toArray(new EnumFacing[0]))),
                (int) (0.6 * 0xFF) << 24);
    }

    private static final ResourceLocation TEX_IO = new ResourceLocation(ROSConstants.MODID,
            "textures/effects/overlay_input_output.png");
    private static final ResourceLocation TEX_O  = new ResourceLocation(ROSConstants.MODID,
            "textures/effects/overlay_input.png");
    private static final ResourceLocation TEX_I  = new ResourceLocation(ROSConstants.MODID,
            "textures/effects/overlay_output.png");


    private static void renderFluid(World w, BlockPos pos, MultiblockSide side, FluidIOPoint point)
            throws ExecutionException
    {
        List<EnumFacing> facings = new ArrayList<>(6);
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (facing == side.getFacing().getOpposite())
                facings.add(facing);
            if (w.getBlockState(pos.offset(facing)).getBlock() == ROSBlocks.FLUID_PIPE)
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

        RenderUtil.renderQuads(PipeOBJStates.fluidPipeCache.get(
                PipeOBJStates.getVisibilityState(facings.toArray(new EnumFacing[0]))),
                (int) (0.6 * 0xFF) << 24);
    }

    private static void renderBelt(World w, BlockPos pos, String connText, EnumFacing facing) throws ExecutionException
    {
        if (!StringUtils.isEmpty(connText) && Minecraft.getMinecraft().player.getPosition().distanceSq(pos) < 12)
            RenderUtil.renderTextOnSide(0.5, 1.25, 0.5, 0.012, 0,
                    Minecraft.getMinecraft().player.getHorizontalFacing().getOpposite(),
                    I18n.format("conn." + connText + ".name"), Color.LIGHT_GRAY);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        RenderUtil.renderQuads(PipeOBJStates.beltCache.get(facing), (int) (0.6 * 0xFF) << 24);
    }
}
