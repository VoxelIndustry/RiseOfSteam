package net.ros.common.init;

import net.minecraft.block.material.Material;
import net.ros.common.block.*;
import net.ros.common.block.creative.BlockCreativeSteamGenerator;
import net.ros.common.block.creative.BlockCreativeWaterGenerator;
import net.ros.common.multiblock.TileMultiblockGag;
import net.ros.common.tile.TileStructure;
import net.ros.common.tile.creative.TileCreativeSteamGenerator;
import net.ros.common.tile.creative.TileCreativeWaterGenerator;
import net.ros.common.tile.machine.*;

import static net.ros.common.init.ROSBlocks.registerBlock;
import static net.ros.common.init.ROSBlocks.registerTile;

public class MachineBlocks
{
    public static void init()
    {
        registerBlock(new BlockKeypunch());
        registerBlock(new BlockTank("fluidtank_small", 0));
        registerBlock(new BlockTank("fluidtank_medium", 1));
        registerBlock(new BlockTank("fluidtank_big", 2));
        registerBlock(new BlockSolidBoiler());
        registerBlock(
                new BlockMultiModularMachine<>("assembler", Material.IRON, TileAssembler::new, TileAssembler.class));
        registerBlock(new BlockCreativeSteamGenerator());
        registerBlock(new BlockCreativeWaterGenerator());
        registerBlock(new BlockStructure());
        registerBlock(new BlockMultiModularMachine<>("steamfurnacemk1", Material.IRON, TileSteamFurnace::new,
                TileSteamFurnace.class));
        registerBlock(new BlockSolarBoiler());
        registerBlock(new BlockSolarMirror());
        registerBlock(new BlockMultiModularMachine<>("rollingmill", Material.IRON, TileRollingMill::new,
                TileRollingMill.class));
        registerBlock(new BlockLiquidBoiler());
        registerBlock(new BlockMultiModularMachine<>("steamfurnacemk2", Material.IRON,
                TileSteamFurnaceMK2::new, TileSteamFurnaceMK2.class));
        registerBlock(new BlockMultiModularMachine<>("sawmill", Material.IRON, TileSawMill::new,
                TileSawMill.class));
        registerBlock(
                new BlockMultiModularMachine<>("orewasher", Material.IRON, TileOreWasher::new, TileOreWasher.class));
        registerBlock(new BlockMultiModularMachine<>("sortingmachine", Material.IRON,
                TileSortingMachine::new, TileSortingMachine.class));
        registerBlock(new BlockMultiModularMachine<>("smallminingdrill", Material.IRON,
                TileSmallMiningDrill::new, TileSmallMiningDrill.class));
        registerBlock(new BlockMultiModularMachine<>("tinyminingdrill", Material.IRON,
                TileTinyMiningDrill::new, TileTinyMiningDrill.class));
        registerBlock(new BlockMultiModularMachine<>("alloycauldron", Material.IRON, TileAlloyCauldron::new,
                TileAlloyCauldron.class));
        registerBlock(new BlockMultiModularMachine<>("capsulefiller", Material.IRON, TileCapsuleFiller::new,
                TileCapsuleFiller.class));
        registerBlock(new BlockMultiModularMachine<>("steamtank_small", Material.IRON,
                () -> new TileSteamTank(0), TileSteamTank.class));
        registerBlock(new BlockMultiModularMachine<>("steamtank_medium", Material.IRON,
                () -> new TileSteamTank(1), TileSteamTank.class));

        registerBlock(new BlockEngineerStorage());
        registerBlock(new BlockEngineerWorkbench());
        registerBlock(new BlockCraftCardLibrary());
        registerBlock(new BlockBlueprintPrinter());

        registerBlock(new BlockBase("gearbox", Material.IRON));
        registerBlock(new BlockBase("logicbox", Material.IRON));

        registerTile(TileTank.class, "tank");
        registerTile(TileKeypunch.class, "keypunch");
        registerTile(TileSolidBoiler.class, "boiler");
        registerTile(TileAssembler.class, "assembler");
        registerTile(TileMultiblockGag.class, "multiblockgag");
        registerTile(TileCreativeSteamGenerator.class, "creative_steam_generator");
        registerTile(TileCreativeWaterGenerator.class, "creative_water_generator");
        registerTile(TileStructure.class, "structure");
        registerTile(TileSteamFurnace.class, "steamfurnace");
        registerTile(TileSolarBoiler.class, "solarboiler");
        registerTile(TileSolarMirror.class, "solarmirror");
        registerTile(TileRollingMill.class, "rollingmill");
        registerTile(TileLiquidBoiler.class, "liquidfuelboiler");
        registerTile(TileSteamFurnaceMK2.class, "steamfurnacemk2");
        registerTile(TileOreWasher.class, "orewasher");
        registerTile(TileSortingMachine.class, "sortingmachine");
        registerTile(TileSmallMiningDrill.class, "smallminingdrill");
        registerTile(TileTinyMiningDrill.class, "tinyminingdrill");
        registerTile(TileAlloyCauldron.class, "alloycauldron");
        registerTile(TileSawMill.class, "sawmill");
        registerTile(TileEngineerStorage.class, "engineerstorage");
        registerTile(TileEngineerWorkbench.class, "engineerworkbench");
        registerTile(TileBlueprintPrinter.class, "blueprintprinter");
        registerTile(TileCraftCardLibrary.class, "craftcardlibrary");
        registerTile(TileCapsuleFiller.class, "capsulefiller");
        registerTile(TileSteamTank.class, "steamtank");
    }
}
