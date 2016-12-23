package net.qbar.common.init;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.QBar;
import net.qbar.common.fluid.BlockQBarFluid;

/**
 * @author Ourten 22 déc. 2016
 */
public class QBarFluids
{
    private static Fluid                 fluidSteam;
    private static BlockQBarFluid        blockFluidSteam;

    @SideOnly(Side.CLIENT)
    private static ModelResourceLocation fluidSteamLocation = new ModelResourceLocation(QBar.MODID + ":" + "blockfluid",
            "steam");

    public static final void registerFluids()
    {
        QBarFluids.fluidSteam = new Fluid("steam", new ResourceLocation(QBar.MODID + ":blocks/fluid/steam_still"),
                new ResourceLocation(QBar.MODID + ":blocks/fluid/steam_flow")).setDensity(-1000).setViscosity(500)
                        .setGaseous(true);
        if (!FluidRegistry.registerFluid(QBarFluids.fluidSteam))
            QBarFluids.fluidSteam = FluidRegistry.getFluid("steam");
        FluidRegistry.addBucketForFluid(QBarFluids.fluidSteam);

        QBarFluids.blockFluidSteam = new BlockQBarFluid(QBarFluids.fluidSteam, Material.WATER, "blockfluidsteam");
        QBarBlocks.registerBlock(QBarFluids.blockFluidSteam, "blockfluidsteam");

        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            ModelLoader.setCustomStateMapper(QBarFluids.blockFluidSteam, new StateMapperBase()
            {
                @Override
                protected ModelResourceLocation getModelResourceLocation(final IBlockState state)
                {
                    return QBarFluids.fluidSteamLocation;
                }
            });

            ModelBakery.registerItemVariants(Item.getItemFromBlock(QBarFluids.blockFluidSteam));
            ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(QBarFluids.blockFluidSteam),
                    stack -> QBarFluids.fluidSteamLocation);
        }
    }
}
