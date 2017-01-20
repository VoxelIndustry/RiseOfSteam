package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileSteamPipe;

public class BlockSteamPipe extends BlockMachineBase
{
    public BlockSteamPipe()
    {
        super("steampipe", Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileSteamPipe(8);
    }
}
