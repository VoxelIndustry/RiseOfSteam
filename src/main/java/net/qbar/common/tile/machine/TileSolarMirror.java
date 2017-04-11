package net.qbar.common.tile.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.TRSRTransformation;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.QBarTileBase;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class TileSolarMirror extends QBarTileBase implements ITileMultiblockCore
{
    private int mirrorCount;

    public TileSolarMirror()
    {
        this.mirrorCount = 0;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setInteger("mirrorCount", this.mirrorCount);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        int previousMirrorCount = this.mirrorCount;
        this.mirrorCount = tag.getInteger("mirrorCount");

        if (this.isClient() && previousMirrorCount != this.mirrorCount)
            this.updateState();
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }

    ////////////
    // RENDER //
    ////////////

    public final VisibilityModelState state = new VisibilityModelState();

    public void updateState()
    {
        if (this.isServer())
        {
            this.sync();
            return;
        }

        this.state.parts.clear();

        for (int i = 0; i < 8; i++)
        {
            if (i >= this.mirrorCount)
                continue;
            this.state.parts.add("AddedMiror" + i);
            for (int j = 1; j < 8; j++)
                this.state.parts.add("Miror Component" + i + " " + j);
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }
}
