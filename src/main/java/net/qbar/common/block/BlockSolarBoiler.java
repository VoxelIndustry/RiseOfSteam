package net.qbar.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.qbar.common.IWrenchable;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.ITileMultiblock;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileSolarBoiler;

public class BlockSolarBoiler extends BlockMultiblockBase implements IWrenchable
{
    public BlockSolarBoiler()
    {
        super("solar_boiler", Material.IRON, Multiblocks.SOLAR_BOILER);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileSolarBoiler();
    }

    @Override
    public boolean onWrench(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
            IBlockState state, ItemStack wrench)
    {

        return false;
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
