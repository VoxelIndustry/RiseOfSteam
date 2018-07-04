package net.ros.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.ros.common.ROSConstants;
import net.ros.common.fluid.BlockFluidBase;
import net.ros.common.ore.Ores;
import net.ros.common.recipe.Materials;

import java.text.NumberFormat;
import java.util.LinkedHashMap;

public class ROSFluids
{
    private static NumberFormat percentFormatter = NumberFormat.getPercentInstance();

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
                    Fluid moltenMetal = new Fluid("molten" + metal.getName(),
                            new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + metal.getName() + "_still"),
                            new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + metal.getName() + "_flow"));
                    FluidRegistry.registerFluid(moltenMetal);
                    FluidRegistry.addBucketForFluid(moltenMetal);

                    FLUIDS.put(moltenMetal, new BlockFluidBase(moltenMetal, Material.LAVA,
                            "blockmolten" + metal.getName()));
                });

        Ores.ORES.stream().filter(ore -> !FluidRegistry.isFluidRegistered("sludge" + ore.getName())).forEach(ore ->
        {
            Fluid sludge = new Fluid("sludge" + ore.getName(),
                    new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + ore.getName() + "_still"),
                    new ResourceLocation(ROSConstants.MODID + ":blocks/fluid/" + ore.getName() + "_flow"));

            FluidRegistry.registerFluid(sludge);
            FluidRegistry.addBucketForFluid(sludge);

            BlockFluidBase blockFluid = new BlockFluidBase(sludge, Material.LAVA,
                    "block" + sludge.getName());

            if (FMLCommonHandler.instance().getEffectiveSide().isClient())
                ore.getMinerals().forEach((mineral, value) -> blockFluid.addInformation(I18n.format(mineral.getName()) + " : " + percentFormatter.format(value)));

            FLUIDS.put(sludge, blockFluid);
        });

        FLUIDS.values().forEach(ROSBlocks::registerBlock);
    }
}
