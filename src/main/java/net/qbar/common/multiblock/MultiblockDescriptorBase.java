package net.qbar.common.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class MultiblockDescriptorBase implements IMultiblockDescriptor
{
    private final String        name;

    private final int           width, height, length, offsetX, offsetY, offsetZ;

    private final AxisAlignedBB XPOSCACHED_AABB;
    private final AxisAlignedBB XNEGCACHED_AABB;
    private final AxisAlignedBB ZPOSCACHED_AABB;
    private final AxisAlignedBB ZNEGCACHED_AABB;

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

        this.XPOSCACHED_AABB = new AxisAlignedBB(-this.getOffsetZ(), -this.getOffsetY(), -this.getOffsetX() - 1,
                this.getLength() - this.getOffsetZ(), this.getHeight() - this.getOffsetY(),
                this.getWidth() - this.getOffsetX() - 1);
        this.ZPOSCACHED_AABB = new AxisAlignedBB(-this.getOffsetX(), -this.getOffsetY(), -this.getOffsetZ(),
                this.getWidth() - this.getOffsetX(), this.getHeight() - this.getOffsetY(),
                this.getLength() - this.getOffsetZ());

        this.XNEGCACHED_AABB = new AxisAlignedBB(-this.getOffsetZ() - 1, -this.getOffsetY(), -this.getOffsetX(),
                this.getLength() - this.getOffsetZ() - 1, this.getHeight() - this.getOffsetY(),
                this.getWidth() - this.getOffsetX());
        this.ZNEGCACHED_AABB = new AxisAlignedBB(-this.getOffsetX() - 1, -this.getOffsetY(), -this.getOffsetZ() - 1,
                this.getWidth() - this.getOffsetX() - 1, this.getHeight() - this.getOffsetY(),
                this.getLength() - this.getOffsetZ() - 1);
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

    @Override
    public AxisAlignedBB getBox(final EnumFacing facing)
    {
        if (facing.getAxis() == Axis.Z)
        {
            if (this.getLength() % 2 == 0 && facing.getAxisDirection() == AxisDirection.NEGATIVE)
                return this.ZNEGCACHED_AABB;
            return this.ZPOSCACHED_AABB;
        }
        if (this.getWidth() % 2 == 0)
        {
            if (facing.getAxisDirection() == AxisDirection.POSITIVE)
                return this.XPOSCACHED_AABB;
            return this.XNEGCACHED_AABB;
        }
        return this.XPOSCACHED_AABB.offset(0, 0, 1);
    }

    @Override
    public Iterable<BlockPos> getAllInBox(final BlockPos pos, final EnumFacing facing)
    {
        Iterable<BlockPos> searchables = null;
        if (facing.getAxis().equals(Axis.Z))
        {
            if (this.getLength() % 2 == 0 && facing.getAxisDirection() == AxisDirection.NEGATIVE)
            {
                searchables = BlockPos.getAllInBox(
                        pos.subtract(new Vec3i(this.getOffsetX() + 1, this.getOffsetY(), this.getOffsetZ() + 1)),
                        pos.add(this.getWidth() - 1 - this.getOffsetX() - 1, this.getHeight() - 1 - this.getOffsetY(),
                                this.getLength() - 1 - this.getOffsetZ() - 1));

            }
            else
            {
                searchables = BlockPos
                        .getAllInBox(pos.subtract(new Vec3i(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ())),
                                pos.add(this.getWidth() - 1 - this.getOffsetX(),
                                        this.getHeight() - 1 - this.getOffsetY(),
                                        this.getLength() - 1 - this.getOffsetZ()));
            }
        }
        else
        {
            if (this.getWidth() % 2 == 0)
            {
                if (facing.getAxisDirection() == AxisDirection.POSITIVE)
                {
                    searchables = BlockPos.getAllInBox(
                            pos.subtract(new Vec3i(this.getOffsetZ(), this.getOffsetY(), this.getOffsetX() + 1)),
                            pos.add(this.getLength() - 1 - this.getOffsetZ(), this.getHeight() - 1 - this.getOffsetY(),
                                    this.getWidth() - 1 - this.getOffsetX() - 1));

                }
                else
                    searchables = BlockPos.getAllInBox(
                            pos.subtract(new Vec3i(this.getOffsetZ() + 1, this.getOffsetY(), this.getOffsetX())),
                            pos.add(this.getLength() - 1 - this.getOffsetZ() - 1,
                                    this.getHeight() - 1 - this.getOffsetY(), this.getWidth() - 1 - this.getOffsetX()));
            }
            else
            {
                searchables = BlockPos
                        .getAllInBox(pos.subtract(new Vec3i(this.getOffsetZ(), this.getOffsetY(), this.getOffsetX())),
                                pos.add(this.getLength() - 1 - this.getOffsetZ(),
                                        this.getHeight() - 1 - this.getOffsetY(),
                                        this.getWidth() - 1 - this.getOffsetX()));
            }
        }
        return searchables;
    }
}
