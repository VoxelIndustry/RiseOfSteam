package net.qbar.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.qbar.common.QBarConstants;
import net.qbar.common.fluid.BlockQBarFluid;
import net.qbar.common.recipe.QBarMaterials;

import java.util.LinkedHashMap;

public class QBarFluids
{
    public static Fluid fluidSteam;

    public static LinkedHashMap<Fluid, BlockQBarFluid> FLUIDS = new LinkedHashMap<>();

    public static void registerFluids()
    {
        QBarFluids.fluidSteam = new Fluid("steam", new ResourceLocation(QBarConstants.MODID +
                ":blocks/fluid/steam_still"), new ResourceLocation(QBarConstants.MODID + ":blocks/fluid/steam_flow"))
                .setDensity(-1000).setViscosity(500).setGaseous(true);
        if (!FluidRegistry.registerFluid(QBarFluids.fluidSteam))
            QBarFluids.fluidSteam = FluidRegistry.getFluid("steam");
        FluidRegistry.addBucketForFluid(QBarFluids.fluidSteam);

        FLUIDS.put(fluidSteam, new BlockQBarFluid(QBarFluids.fluidSteam, Material.WATER, "blockfluidsteam"));

        QBarMaterials.metals.stream().filter(metal -> !FluidRegistry.isFluidRegistered("molten" + metal))
                .forEach(metal ->
                {
                    Fluid moltenMetal = new Fluid("molten" + metal,
                            new ResourceLocation(QBarConstants.MODID + ":blocks/fluid/" + metal + "_still"),
                            new ResourceLocation(QBarConstants.MODID + ":blocks/fluid/" + metal + "_flow"));
                    FluidRegistry.registerFluid(moltenMetal);
                    FluidRegistry.addBucketForFluid(moltenMetal);

                    FLUIDS.put(moltenMetal, new BlockQBarFluid(moltenMetal, Material.LAVA, "blockmolten" + metal));
                });

        FLUIDS.values().forEach(QBarBlocks::registerBlock);
    }
}
