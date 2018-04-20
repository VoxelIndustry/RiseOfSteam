package net.qbar.common.init;

import net.minecraft.block.material.Material;
import net.qbar.common.block.*;
import net.qbar.common.block.creative.BlockCreativeSteamGenerator;
import net.qbar.common.block.creative.BlockCreativeWaterGenerator;
import net.qbar.common.multiblock.TileMultiblockGag;
import net.qbar.common.tile.TileStructure;
import net.qbar.common.tile.creative.TileCreativeSteamGenerator;
import net.qbar.common.tile.creative.TileCreativeWaterGenerator;
import net.qbar.common.tile.machine.*;

public class MachineBlocks
{
    public static void init()
    {
        QBarBlocks.registerBlock(new BlockKeypunch());
        QBarBlocks.registerBlock(new BlockTank("fluidtank_small", 0));
        QBarBlocks.registerBlock(new BlockTank("fluidtank_medium", 1));
        QBarBlocks.registerBlock(new BlockTank("fluidtank_big", 2));
        QBarBlocks.registerBlock(new BlockSolidBoiler());
        QBarBlocks.registerBlock(
                new BlockMultiModularMachine<>("assembler", Material.IRON, TileAssembler::new, TileAssembler.class));
        QBarBlocks.registerBlock(new BlockCreativeSteamGenerator());
        QBarBlocks.registerBlock(new BlockCreativeWaterGenerator());
        QBarBlocks.registerBlock(new BlockStructure());
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("steamfurnacemk1", Material.IRON, TileSteamFurnace::new,
                TileSteamFurnace.class));
        QBarBlocks.registerBlock(new BlockSolarBoiler());
        QBarBlocks.registerBlock(new BlockSolarMirror());
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("rollingmill", Material.IRON, TileRollingMill::new,
                TileRollingMill.class));
        QBarBlocks.registerBlock(new BlockLiquidBoiler());
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("steamfurnacemk2", Material.IRON,
                TileSteamFurnaceMK2::new, TileSteamFurnaceMK2.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("sawmill", Material.IRON, TileSawMill::new,
                TileSawMill.class));
        QBarBlocks.registerBlock(
                new BlockMultiModularMachine<>("orewasher", Material.IRON, TileOreWasher::new, TileOreWasher.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("sortingmachine", Material.IRON,
                TileSortingMachine::new, TileSortingMachine.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("smallminingdrill", Material.IRON,
                TileSmallMiningDrill::new, TileSmallMiningDrill.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("tinyminingdrill", Material.IRON,
                TileTinyMiningDrill::new, TileTinyMiningDrill.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("alloycauldron", Material.IRON, TileAlloyCauldron::new,
                TileAlloyCauldron.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("capsulefiller", Material.IRON, TileCapsuleFiller::new,
                TileCapsuleFiller.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("steamtank_small", Material.IRON,
                () -> new TileSteamTank(0), TileSteamTank.class));
        QBarBlocks.registerBlock(new BlockMultiModularMachine<>("steamtank_medium", Material.IRON,
                () -> new TileSteamTank(1), TileSteamTank.class));

        QBarBlocks.registerBlock(new BlockEngineerStorage());
        QBarBlocks.registerBlock(new BlockEngineerWorkbench());
        QBarBlocks.registerBlock(new BlockCraftCardLibrary());
        QBarBlocks.registerBlock(new BlockBlueprintPrinter());

        QBarBlocks.registerBlock(new BlockBase("gearbox", Material.IRON));
        QBarBlocks.registerBlock(new BlockBase("logicbox", Material.IRON));

        QBarBlocks.registerTile(TileTank.class, "tank");
        QBarBlocks.registerTile(TileKeypunch.class, "keypunch");
        QBarBlocks.registerTile(TileSolidBoiler.class, "boiler");
        QBarBlocks.registerTile(TileAssembler.class, "assembler");
        QBarBlocks.registerTile(TileMultiblockGag.class, "multiblockgag");
        QBarBlocks.registerTile(TileCreativeSteamGenerator.class, "creative_steam_generator");
        QBarBlocks.registerTile(TileCreativeWaterGenerator.class, "creative_water_generator");
        QBarBlocks.registerTile(TileStructure.class, "structure");
        QBarBlocks.registerTile(TileSteamFurnace.class, "steamfurnace");
        QBarBlocks.registerTile(TileSolarBoiler.class, "solarboiler");
        QBarBlocks.registerTile(TileSolarMirror.class, "solarmirror");
        QBarBlocks.registerTile(TileRollingMill.class, "rollingmill");
        QBarBlocks.registerTile(TileLiquidBoiler.class, "liquidfuelboiler");
        QBarBlocks.registerTile(TileSteamFurnaceMK2.class, "steamfurnacemk2");
        QBarBlocks.registerTile(TileOreWasher.class, "orewasher");
        QBarBlocks.registerTile(TileSortingMachine.class, "sortingmachine");
        QBarBlocks.registerTile(TileSmallMiningDrill.class, "smallminingdrill");
        QBarBlocks.registerTile(TileTinyMiningDrill.class, "tinyminingdrill");
        QBarBlocks.registerTile(TileAlloyCauldron.class, "alloycauldron");
        QBarBlocks.registerTile(TileSawMill.class, "sawmill");
        QBarBlocks.registerTile(TileEngineerStorage.class, "engineerstorage");
        QBarBlocks.registerTile(TileEngineerWorkbench.class, "engineerworkbench");
        QBarBlocks.registerTile(TileBlueprintPrinter.class, "blueprintprinter");
        QBarBlocks.registerTile(TileCraftCardLibrary.class, "craftcardlibrary");
        QBarBlocks.registerTile(TileCapsuleFiller.class, "capsulefiller");
        QBarBlocks.registerTile(TileSteamTank.class, "steamtank");
    }
}
