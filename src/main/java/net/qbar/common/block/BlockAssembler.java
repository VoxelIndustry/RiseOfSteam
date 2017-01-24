package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.qbar.common.tile.TileAssembler;

public class BlockAssembler extends BlockMachineBase
{
    public BlockAssembler()
    {
        super("assembler", Material.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta)
    {
        return new TileAssembler();
    }
}
