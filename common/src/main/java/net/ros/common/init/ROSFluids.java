package net.ros.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.ros.common.ROSConstants;
import net.ros.common.fluid.BlockFluidBase;
import net.ros.common.recipe.Materials;

import java.util.LinkedHashMap;

public class ROSFluids
{
    public static Fluid fluidSteam;

    public static LinkedHashMap<Fluid, BlockFluidBase> FLUIDS = new LinkedHashMap<>();

    public static void registerFluids()
    {
        ROSFluids.fluidSteam = new Fluid("steam", new ResourceLocation(ROSConstants.MODID +
                ":blocks/fluid/steam_still"), new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/steam_flow"))
                .setDensity(-1000).setViscosity(500).setGaseous(true);
        if (!FluidRegistry.registerFluid(ROSFluids.fluidSteam))
            ROSFluids.fluidSteam = FluidRegistry.getFluid("steam");
        FluidRegistry.addBucketForFluid(ROSFluids.fluidSteam);

        FLUIDS.put(fluidSteam, new BlockFluidBase(ROSFluids.fluidSteam, Material.WATER, "blockfluidsteam"));

        Materials.metals.stream().filter(metal -> !FluidRegistry.isFluidRegistered("molten" + metal))
                .forEach(metal ->
                {
                    Fluid moltenMetal = new Fluid("molten" + metal,
                            new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + metal + "_still"),
                            new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + metal + "_flow"));
                    FluidRegistry.registerFluid(moltenMetal);
                    FluidRegistry.addBucketForFluid(moltenMetal);

                    FLUIDS.put(moltenMetal, new BlockFluidBase(moltenMetal, Material.LAVA, "blockmolten" + metal));
                });

        FLUIDS.values().forEach(ROSBlocks::registerBlock);
    }
}
