package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.IWrenchable;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.Multiblocks;
import net.qbar.common.tile.machine.TileTank;

public class BlockTank extends BlockMultiblockBase implements IWrenchable
{
    public BlockTank()
    {
        super("fluidtank", Material.IRON, Multiblocks.FLUID_TANK);
    }

    @Override
    public boolean rotateBlock(final World world, final BlockPos pos, final EnumFacing facing)
    {
        if (facing == null || !EnumFacing.Plane.HORIZONTAL.apply(facing))
            return false;
        final NBTTagCompound previous = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockMultiblockBase.FACING, facing));
        world.getTileEntity(pos).readFromNBT(previous);
        return true;
    }

    @Override
    public TileEntity getTile(final World w, final IBlockState state)
    {
        return new TileTank();
    }

    @Override
    public boolean onWrench(final EntityPlayer player, final World world, final BlockPos pos, final EnumHand hand,
            final EnumFacing facing, final IBlockState state)
    {
        this.rotateBlock(world, pos, state.getValue(BlockMultiblockBase.FACING).rotateAround(Axis.Y));
        return true;
    }
}
