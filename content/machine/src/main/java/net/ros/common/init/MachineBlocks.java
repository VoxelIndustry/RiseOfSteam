package net.ros.common.init;

import net.minecraft.block.material.Material;
import net.ros.common.block.*;
import net.ros.common.block.creative.BlockCreativeSteamGenerator;
import net.ros.common.block.creative.BlockCreativeWaterGenerator;
import net.ros.common.multiblock.TileMultiblockGag;
import net.ros.common.tile.machine.*;
import net.ros.common.tile.TileStructure;
import net.ros.common.tile.creative.TileCreativeSteamGenerator;
import net.ros.common.tile.creative.TileCreativeWaterGenerator;

public class MachineBlocks
{
    public static void init()
    {
        ROSBlocks.registerBlock(new BlockKeypunch());
        ROSBlocks.registerBlock(new BlockTank("fluidtank_small", 0));
        ROSBlocks.registerBlock(new BlockTank("fluidtank_medium", 1));
        ROSBlocks.registerBlock(new BlockTank("fluidtank_big", 2));
        ROSBlocks.registerBlock(new BlockSolidBoiler());
        ROSBlocks.registerBlock(
                new BlockMultiModularMachine<>("assembler", Material.IRON, TileAssembler::new, TileAssembler.class));
        ROSBlocks.registerBlock(new BlockCreativeSteamGenerator());
        ROSBlocks.registerBlock(new BlockCreativeWaterGenerator());
        ROSBlocks.registerBlock(new BlockStructure());
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("steamfurnacemk1", Material.IRON, TileSteamFurnace::new,
                TileSteamFurnace.class));
        ROSBlocks.registerBlock(new BlockSolarBoiler());
        ROSBlocks.registerBlock(new BlockSolarMirror());
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("rollingmill", Material.IRON, TileRollingMill::new,
                TileRollingMill.class));
        ROSBlocks.registerBlock(new BlockLiquidBoiler());
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("steamfurnacemk2", Material.IRON,
                TileSteamFurnaceMK2::new, TileSteamFurnaceMK2.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("sawmill", Material.IRON, TileSawMill::new,
                TileSawMill.class));
        ROSBlocks.registerBlock(
                new BlockMultiModularMachine<>("orewasher", Material.IRON, TileOreWasher::new, TileOreWasher.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("sortingmachine", Material.IRON,
                TileSortingMachine::new, TileSortingMachine.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("smallminingdrill", Material.IRON,
                TileSmallMiningDrill::new, TileSmallMiningDrill.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("tinyminingdrill", Material.IRON,
                TileTinyMiningDrill::new, TileTinyMiningDrill.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("alloycauldron", Material.IRON, TileAlloyCauldron::new,
                TileAlloyCauldron.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("capsulefiller", Material.IRON, TileCapsuleFiller::new,
                TileCapsuleFiller.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("steamtank_small", Material.IRON,
                () -> new TileSteamTank(0), TileSteamTank.class));
        ROSBlocks.registerBlock(new BlockMultiModularMachine<>("steamtank_medium", Material.IRON,
                () -> new TileSteamTank(1), TileSteamTank.class));

        ROSBlocks.registerBlock(new BlockEngineerStorage());
        ROSBlocks.registerBlock(new BlockEngineerWorkbench());
        ROSBlocks.registerBlock(new BlockCraftCardLibrary());
        ROSBlocks.registerBlock(new BlockBlueprintPrinter());

        ROSBlocks.registerBlock(new BlockBase("gearbox", Material.IRON));
        ROSBlocks.registerBlock(new BlockBase("logicbox", Material.IRON));

        ROSBlocks.registerTile(TileTank.class, "tank");
        ROSBlocks.registerTile(TileKeypunch.class, "keypunch");
        ROSBlocks.registerTile(TileSolidBoiler.class, "boiler");
        ROSBlocks.registerTile(TileAssembler.class, "assembler");
        ROSBlocks.registerTile(TileMultiblockGag.class, "multiblockgag");
        ROSBlocks.registerTile(TileCreativeSteamGenerator.class, "creative_steam_generator");
        ROSBlocks.registerTile(TileCreativeWaterGenerator.class, "creative_water_generator");
        ROSBlocks.registerTile(TileStructure.class, "structure");
        ROSBlocks.registerTile(TileSteamFurnace.class, "steamfurnace");
        ROSBlocks.registerTile(TileSolarBoiler.class, "solarboiler");
        ROSBlocks.registerTile(TileSolarMirror.class, "solarmirror");
        ROSBlocks.registerTile(TileRollingMill.class, "rollingmill");
        ROSBlocks.registerTile(TileLiquidBoiler.class, "liquidfuelboiler");
        ROSBlocks.registerTile(TileSteamFurnaceMK2.class, "steamfurnacemk2");
        ROSBlocks.registerTile(TileOreWasher.class, "orewasher");
        ROSBlocks.registerTile(TileSortingMachine.class, "sortingmachine");
        ROSBlocks.registerTile(TileSmallMiningDrill.class, "smallminingdrill");
        ROSBlocks.registerTile(TileTinyMiningDrill.class, "tinyminingdrill");
        ROSBlocks.registerTile(TileAlloyCauldron.class, "alloycauldron");
        ROSBlocks.registerTile(TileSawMill.class, "sawmill");
        ROSBlocks.registerTile(TileEngineerStorage.class, "engineerstorage");
        ROSBlocks.registerTile(TileEngineerWorkbench.class, "engineerworkbench");
        ROSBlocks.registerTile(TileBlueprintPrinter.class, "blueprintprinter");
        ROSBlocks.registerTile(TileCraftCardLibrary.class, "craftcardlibrary");
        ROSBlocks.registerTile(TileCapsuleFiller.class, "capsulefiller");
        ROSBlocks.registerTile(TileSteamTank.class, "steamtank");
    }
}
