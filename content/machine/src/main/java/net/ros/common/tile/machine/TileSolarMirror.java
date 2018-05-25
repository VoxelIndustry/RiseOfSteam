package net.ros.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.init.ROSItems;
import net.ros.common.multiblock.ITileMultiblockCore;
import net.ros.common.tile.TileBase;
import net.ros.client.render.tile.VisibilityModelState;

import javax.annotation.Nullable;

public class TileSolarMirror extends TileBase implements ITileMultiblockCore
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
            this.askServerSync();
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
            if (i <= this.mirrorCount)
                continue;
            this.state.parts.add("AddedMiror" + i);
            for (int j = 1; j < 8; j++)
                this.state.parts.add("MirorComponent" + i + " " + j);
        }

        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.pos, false);
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

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        if (this.isServer() && player.inventory.getCurrentItem().getItem() == ROSItems.SOLAR_REFLECTOR
                && this.mirrorCount < 6)
        {
            this.mirrorCount++;
            if (!player.capabilities.isCreativeMode)
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
            this.sync();
        }
        return true;
    }

    public int getMirrorCount()
    {
        return mirrorCount;
    }
}
