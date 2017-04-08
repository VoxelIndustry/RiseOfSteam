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
    private float             horizontalAngle;

    public TileSolarMirror()
    {
        this.solarBoilerPos = BlockPos.ORIGIN;
        this.horizontalAngle = 0.0f;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setLong("solarBoilerPos", this.solarBoilerPos.toLong());
        tag.setFloat("horizontalAngle", this.horizontalAngle);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        float previousAngle = this.horizontalAngle;

        this.solarBoilerPos = BlockPos.fromLong(tag.getLong("solarBoilerPos"));
        this.horizontalAngle = tag.getFloat("horizontalAngle");

        if (this.isClient() && previousAngle != this.horizontalAngle)
            this.updateState();
    }

    public BlockPos getSolarBoilerPos()
    {
        return solarBoilerPos;
    }

    public void setSolarBoilerPos(BlockPos solarBoilerPos)
    {
        this.solarBoilerPos = solarBoilerPos;
    }

    public float getHorizontalAngle()
    {
        return horizontalAngle;
    }

    public void setHorizontalAngle(float horizontalAngle)
    {
        this.horizontalAngle = horizontalAngle;
        this.sync();
    }

    public void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }

        AxisAngle4d yaw = new AxisAngle4d(0, 1, 0, Math.toRadians(horizontalAngle));
        AxisAngle4d pitch = new AxisAngle4d(1, 0, 0, 0);
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
