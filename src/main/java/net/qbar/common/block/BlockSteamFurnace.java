package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.TileSteamFurnace;

public class BlockSteamFurnace extends BlockMultiblockBase
{
    public BlockSteamFurnace()
    {
        super("steamfurnacemk1", Material.IRON, Multiblocks.STEAM_FURNACE_MK1);
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileSteamFurnace();
    }
}