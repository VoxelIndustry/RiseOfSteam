package net.qbar.common.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class MultiblockSide
{
    private final BlockPos   pos;
    private final EnumFacing facing;

    public MultiblockSide(BlockPos pos, EnumFacing facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MultiblockSide that = (MultiblockSide) o;

        if (pos != null ? !pos.equals(that.pos) : that.pos != null)
            return false;
        return facing == that.facing;
    }

    @Override
    public int hashCode()
    {
        int result = pos != null ? pos.hashCode() : 0;
        result = 31 * result + (facing != null ? facing.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "MultiblockSide{" + "pos=" + pos + ", facing=" + facing + '}';
    }
}
