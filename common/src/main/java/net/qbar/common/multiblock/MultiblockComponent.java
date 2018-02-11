package net.qbar.common.multiblock;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.qbar.common.machine.IMachineComponent;
import net.qbar.common.machine.MachineDescriptor;

import java.util.EnumMap;

public class MultiblockComponent implements IMachineComponent
{
    @Getter
    private final int width, height, length, offsetX, offsetY, offsetZ;

    private final EnumMap<EnumFacing, BlockPos>      CORE_OFFSET;
    private final EnumMap<EnumFacing, AxisAlignedBB> CACHED_AABB;

    @Getter
    @Setter
    private MachineDescriptor descriptor;

    public MultiblockComponent(int width, int height, int length, int offsetX, int offsetY, int offsetZ)
    {
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

    public AxisAlignedBB getBox(final EnumFacing facing)
    {
        return this.CACHED_AABB.get(facing);
    }

    public Iterable<BlockPos> getAllInBox(BlockPos pos, final EnumFacing facing)
    {
        Iterable<BlockPos> searchables;

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

    public MultiblockSide worldSideToMultiblockSide(MultiblockSide side, EnumFacing orientation)
    {
        EnumFacing resultFacing = side.getFacing();
        BlockPos resultPos = side.getPos();

        if (orientation == EnumFacing.EAST)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.rotateY();
            resultPos = new BlockPos(-resultPos.getZ(), resultPos.getY(), resultPos.getX());
        }
        else if (orientation == EnumFacing.WEST)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.rotateYCCW();
            resultPos = new BlockPos(resultPos.getZ(), resultPos.getY(), -resultPos.getX());
        }
        else if (orientation == EnumFacing.NORTH)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.getOpposite();
            resultPos = new BlockPos(-resultPos.getX(), resultPos.getY(), -resultPos.getZ());
        }
        return new MultiblockSide(resultPos, resultFacing);
    }

    public MultiblockSide multiblockSideToWorldSide(MultiblockSide side, EnumFacing orientation)
    {
        EnumFacing resultFacing = side.getFacing();
        BlockPos resultPos = side.getPos();

        if (orientation == EnumFacing.EAST)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.rotateYCCW();
            resultPos = new BlockPos(resultPos.getZ(), resultPos.getY(), -resultPos.getX());
        }
        else if (orientation == EnumFacing.WEST)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.rotateY();
            resultPos = new BlockPos(-resultPos.getZ(), resultPos.getY(), resultPos.getX());
        }
        else if (orientation == EnumFacing.NORTH)
        {
            if (resultFacing.getAxis().isHorizontal())
                resultFacing = resultFacing.getOpposite();
            resultPos = new BlockPos(-resultPos.getX(), resultPos.getY(), -resultPos.getZ());
        }
        return new MultiblockSide(resultPos, resultFacing);
    }

    public int getBlockCount()
    {
        return this.width * this.height * this.length;
    }
}
