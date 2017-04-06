package net.qbar.common.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.EnumMap;

public class MultiblockDescriptorBase implements IMultiblockDescriptor
{
    private final String                             name;

    private final int                                width, height, length, offsetX, offsetY, offsetZ;

    private final EnumMap<EnumFacing, BlockPos>      CORE_OFFSET;
    private final EnumMap<EnumFacing, AxisAlignedBB> CACHED_AABB;

    MultiblockDescriptorBase(final String name, final int width, final int height, final int length, final int offsetX,
            final int offsetY, final int offsetZ)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.length = length;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;

        this.CORE_OFFSET = new EnumMap<>(EnumFacing.class);
        this.CACHED_AABB = new EnumMap<>(EnumFacing.class);

        for (EnumFacing facing : EnumFacing.VALUES)
        {
            this.CORE_OFFSET.put(facing, this.internalGetCoreOffset(facing));

            if (facing.getAxis() == Axis.Z)
                this.CACHED_AABB.put(facing,
                        new AxisAlignedBB(-this.getOffsetX(), -this.getOffsetY(), -this.getOffsetZ(),
                                this.getWidth() - this.getOffsetX(), this.getHeight() - this.getOffsetY(),
                                this.getLength() - this.getOffsetZ()).offset(this.CORE_OFFSET.get(facing)));
            else
                this.CACHED_AABB.put(facing,
                        new AxisAlignedBB(-this.getOffsetZ(), -this.getOffsetY(), -this.getOffsetX(),
                                this.getLength() - this.getOffsetZ(), this.getHeight() - this.getOffsetY(),
                                this.getWidth() - this.getOffsetX()).offset(this.CORE_OFFSET.get(facing)));
        }
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public int getLength()
    {
        return this.length;
    }

    @Override
    public int getOffsetX()
    {
        return this.offsetX;
    }

    @Override
    public int getOffsetY()
    {
        return this.offsetY;
    }

    @Override
    public int getOffsetZ()
    {
        return this.offsetZ;
    }

    private BlockPos internalGetCoreOffset(EnumFacing facing)
    {
        BlockPos rtn = BlockPos.ORIGIN;

        if (this.getLength() % 2 == 0 || this.getWidth() % 2 == 0)
        {
            if (this.getWidth() % 2 == 0 && facing.getAxis() == Axis.Z
                    && facing.getAxisDirection() == AxisDirection.NEGATIVE)
                rtn = rtn.add(-1, 0, 0);
            if (this.getWidth() % 2 == 0 && facing.getAxis() == Axis.X
                    && facing.getAxisDirection() == AxisDirection.POSITIVE)
                rtn = rtn.add(0, 0, -1);
            if (this.getLength() % 2 == 0 && facing.getAxis() == Axis.Z
                    && facing.getAxisDirection() == AxisDirection.NEGATIVE)
                rtn = rtn.add(0, 0, -1);
            if (this.getLength() % 2 == 0 && facing.getAxis() == Axis.X
                    && facing.getAxisDirection() == AxisDirection.NEGATIVE)
                rtn = rtn.add(-1, 0, 0);
        }
        return rtn;
    }

    public BlockPos getCoreOffset(EnumFacing facing)
    {
        return this.CORE_OFFSET.get(facing);
    }

    @Override
    public AxisAlignedBB getBox(final EnumFacing facing)
    {
        return this.CACHED_AABB.get(facing);
    }

    @Override
    public Iterable<BlockPos> getAllInBox(BlockPos pos, final EnumFacing facing)
    {
        Iterable<BlockPos> searchables = null;

        pos = pos.add(this.getCoreOffset(facing));
        if (facing.getAxis() == Axis.Z)
            searchables = BlockPos.getAllInBox(
                    pos.subtract(new Vec3i(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ())),
                    pos.add(this.getWidth() - 1 - this.getOffsetX(), this.getHeight() - 1 - this.getOffsetY(),
                            this.getLength() - 1 - this.getOffsetZ()));
        else
            searchables = BlockPos.getAllInBox(
                    pos.subtract(new Vec3i(this.getOffsetZ(), this.getOffsetY(), this.getOffsetX())),
                    pos.add(this.getLength() - 1 - this.getOffsetZ(), this.getHeight() - 1 - this.getOffsetY(),
                            this.getWidth() - 1 - this.getOffsetX()));
        return searchables;
    }
}
