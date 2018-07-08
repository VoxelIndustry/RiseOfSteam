package net.ros.client;

import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.ros.client.render.ModelPipeCover;
import net.ros.client.render.ModelPipeInventory;
import net.ros.common.ROSConstants;
import net.ros.common.grid.node.PipeNature;
import net.ros.common.grid.node.PipeSize;
import net.ros.common.recipe.Materials;
import net.ros.common.recipe.Metal;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PipeModelRegistry
{
    private List<Pair<ModelResourceLocation, ModelPipeCover>> models;
    private List<Block>                                       coverBlocks;

    public PipeModelRegistry()
    {
        this.models = new ArrayList<>();
        this.coverBlocks = new ArrayList<>();

        // Valves
        this.replacePipesModel(PipeNature.FLUID, "valve",
                new PipeSize[]{PipeSize.SMALL, PipeSize.MEDIUM}, new Metal[]{Materials.IRON, Materials.CAST_IRON},
                "block/_fluidvalve_small.mwm");
        this.replacePipesModel(PipeNature.STEAM, "valve",
                new PipeSize[]{PipeSize.SMALL, PipeSize.MEDIUM}, new Metal[]{Materials.BRASS, Materials.STEEL},
                "block/steamvalve_small.mwm");

        this.replacePipesModel(PipeNature.FLUID, "valve", new PipeSize[]{PipeSize.LARGE},
                new Metal[]{Materials.IRON, Materials.CAST_IRON}, "block/_fluidvalve_medium.mwm");
        this.replacePipesModel(PipeNature.STEAM, "valve", new PipeSize[]{PipeSize.LARGE},
                new Metal[]{Materials.BRASS, Materials.STEEL}, "block/steamvalve_medium.mwm");

        // Gauges
        this.replacePipesModel(PipeNature.STEAM, "gauge",
                new PipeSize[]{PipeSize.SMALL, PipeSize.MEDIUM}, new Metal[]{Materials.BRASS, Materials.STEEL},
                "block/steamgauge_small.mwm");
        this.replacePipesModel(PipeNature.STEAM, "gauge", new PipeSize[]{PipeSize.LARGE},
                new Metal[]{Materials.BRASS, Materials.STEEL}, "block/steamgauge_medium.mwm");

        // Steam vents
        this.replacePipesModel(PipeNature.STEAM, "vent",
                new PipeSize[]{PipeSize.SMALL, PipeSize.MEDIUM}, new Metal[]{Materials.BRASS, Materials.STEEL},
                "block/steamvent_small.mwm");
        this.replacePipesModel(PipeNature.STEAM, "vent",
                new PipeSize[]{PipeSize.LARGE}, new Metal[]{Materials.BRASS, Materials.STEEL},
                "block/steamvent_large.mwm");

        // Pressure valve
        this.replacePipesModel(PipeNature.STEAM, "pressurevalve",
                new PipeSize[]{PipeSize.SMALL, PipeSize.MEDIUM, PipeSize.LARGE}, new Metal[]{Materials.BRASS,
                        Materials.STEEL},
                "block/steampressurevalve_small.mwm");
    }

    public void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry)
    {
        this.replacePipeInventoryModel(registry, Materials.IRON, Materials.CAST_IRON, Materials.BRASS, Materials.STEEL);

        this.models.forEach(pair -> registry.putObject(pair.getKey(), pair.getValue()));
    }

    public void onModelRegistry()
    {
        this.coverBlocks.forEach(block ->
                ModelLoader.setCustomStateMapper(block, new StateMapperBase()
                {
                    @Override
                    @Nonnull
                    protected ModelResourceLocation getModelResourceLocation(@NonNull IBlockState state)
                    {
                        // TODO: Add special case for restricted facing pipe covers
                        EnumFacing facing = state.getPropertyKeys().contains(BlockDirectional.FACING) ?
                                state.getValue(BlockDirectional.FACING) : EnumFacing.UP;

                        return new ModelResourceLocation(
                                Item.getItemFromBlock(block).getRegistryName(), "facing=" + facing.getName());
                    }
                }));
    }

    public void replacePipesModel(PipeNature nature, String pipeName, PipeSize[] sizes, Metal[] metals,
                                  String modelPath)
    {
        for (Metal metal: metals)
        {
            for (PipeSize size: sizes)
            {
                replacePipeModel(Block.getBlockFromName(ROSConstants.MODID + ":" + nature.toString() + pipeName + "_"
                                + metal.getName() + "_" + size.toString()),
                        Block.getBlockFromName(ROSConstants.MODID + ":" + nature.toString() + "pipe_" +
                                metal.getName() + "_" + size.toString()),
                        new ResourceLocation(ROSConstants.MODID, modelPath));
            }
        }
    }

    private void replacePipeModel(Block block, Block pipeBlock, ResourceLocation modelLocation)
    {
        ModelPipeCover model = new ModelPipeCover(modelLocation, block, pipeBlock);

        coverBlocks.add(block);

        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=up"), model));

        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=down"), model));
        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=east"), model));
        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=west"), model));
        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=south"), model));
        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "facing=north"), model));

        models.add(Pair.of(new ModelResourceLocation(
                Item.getItemFromBlock(block).getRegistryName(), "inventory"), model));
    }

    public void replacePipeInventoryModel(IRegistry<ModelResourceLocation, IBakedModel> registry, Metal... metals)
    {
        for (PipeNature nature: PipeNature.values())
        {
            for (PipeSize size: PipeSize.values())
            {
                for (Metal metal: metals)
                {
                    Block block = Block.getBlockFromName(ROSConstants.MODID + ":" +
                            nature.toString() + "pipe_" + metal.getName() + "_" + size.toString());
                    registry.putObject(new ModelResourceLocation(Item.getItemFromBlock(block).getRegistryName(),
                            "inventory"), new ModelPipeInventory(block));
                }
            }
        }
    }
}
