package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.ros.common.tile.TileSteamVent;

import javax.annotation.Nullable;

public class BlockSteamVent extends BlockMachineBase<TileSteamVent>
{
    public BlockSteamVent()
    {
        super("steamvent", Material.IRON, TileSteamVent.class);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileSteamVent();
    }
}
