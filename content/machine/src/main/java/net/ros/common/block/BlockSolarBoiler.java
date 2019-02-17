package net.ros.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.ros.common.multiblock.ITileMultiblock;
import net.ros.common.tile.machine.TileSolarBoiler;

public class BlockSolarBoiler extends BlockMultiModularMachine<TileSolarBoiler>
{
    public BlockSolarBoiler()
    {
        super("solar_boiler", Material.IRON, TileSolarBoiler::new, TileSolarBoiler.class);
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block block, BlockPos from)
    {
        super.neighborChanged(state, w, pos, block, from);

        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            ITileMultiblock multiblock = (ITileMultiblock) w.getTileEntity(pos);
            if (multiblock != null && multiblock.getCore() != null)
                ((TileSolarBoiler) multiblock.getCore()).checkMirrors();
        }
    }
}
