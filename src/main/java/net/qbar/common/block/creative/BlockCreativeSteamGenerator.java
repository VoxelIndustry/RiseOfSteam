package net.qbar.common.block.creative;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.block.BlockMachineBase;
import net.qbar.common.tile.creative.TileCreativeSteamGenerator;

public class BlockCreativeSteamGenerator extends BlockMachineBase<TileCreativeSteamGenerator>
{
    public BlockCreativeSteamGenerator()
    {
        super("creative_steam_generator", Material.IRON, TileCreativeSteamGenerator.class);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileCreativeSteamGenerator();
    }
}
