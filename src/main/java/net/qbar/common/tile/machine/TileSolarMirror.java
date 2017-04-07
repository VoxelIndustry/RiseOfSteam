package net.qbar.common.tile.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.model.TRSRTransformation;
import net.qbar.common.tile.QBarTileBase;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class TileSolarMirror extends QBarTileBase
{
    public TRSRTransformation transform = TRSRTransformation.identity();
    private BlockPos          solarBoilerPos;

    public TileSolarMirror()
    {
        this.solarBoilerPos = BlockPos.ORIGIN;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setLong("solarBoilerPos", this.solarBoilerPos.toLong());
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        BlockPos previous = this.solarBoilerPos;
        this.solarBoilerPos = BlockPos.fromLong(tag.getLong("solarBoilerPos"));
        if (this.isClient() && !previous.equals(this.solarBoilerPos))
            this.updateState();
    }

    public BlockPos getSolarBoilerPos()
    {
        return solarBoilerPos;
    }

    public void setSolarBoilerPos(BlockPos solarBoilerPos)
    {
        this.solarBoilerPos = solarBoilerPos;
        this.sync();
    }

    public void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }

        BlockPos boilerPos = new BlockPos(this.getSolarBoilerPos().getX(), this.getPos().getY(), this.getSolarBoilerPos().getZ());
        BlockPos lookVec = boilerPos.subtract(this.getPos());
        
        AxisAngle4d yaw = new AxisAngle4d(0, 1, 0, -(Math.atan2(lookVec.getZ(), lookVec.getX()) - Math.PI));
        AxisAngle4d pitch = new AxisAngle4d(1, 0, 0, -(Math.atan2(lookVec.getY(),
                Math.sqrt(lookVec.getX() * lookVec.getX() + lookVec.getZ() * lookVec.getZ()))));
        Quat4f rot = new Quat4f(0, 0, 0, 1);
        Quat4f yawQuat = new Quat4f();
        Quat4f pitchQuat = new Quat4f();
        yawQuat.set(yaw);
        rot.mul(yawQuat);
        pitchQuat.set(pitch);
        rot.mul(pitchQuat);
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        matrix.setRotation(rot);
        transform = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(matrix));
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }
}
