package net.qbar.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.multiblock.blueprint.BlueprintState;
import net.qbar.common.multiblock.blueprint.Blueprints;

public class TileStructure extends QBarTileBase implements ITileMultiblockCore
{
    private Blueprint      blueprint;
    private BlueprintState blueprintState;

    private int            meta;

    private AxisAlignedBB  cachedBB;

    public TileStructure()
    {
    }

    public void stepBuilding(final EntityPlayer player)
    {

    }

    @Override
    public boolean hasFastRenderer()
    {
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        if (this.blueprint != null)
        {
            tag.setString("blueprint", this.blueprint.getName());

            if (this.blueprintState != null)
                tag.setTag("blueprintState", this.blueprintState.toNBT());
        }
        tag.setInteger("statemeta", this.meta);
        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.blueprint = Blueprints.getInstance().getBlueprint(tag.getString("blueprint"));
        if (this.blueprint != null)
            this.blueprintState = new BlueprintState(this.blueprint, tag.getCompoundTag("blueprintState"));
        this.meta = tag.getInteger("statemeta");
    }

    public void setBlueprint(final Blueprint blueprint)
    {
        this.blueprint = blueprint;
        this.blueprintState = new BlueprintState(blueprint);
    }

    public Blueprint getBlueprint()
    {
        return this.blueprint;
    }

    public BlueprintState getBlueprintState()
    {
        return this.blueprintState;
    }

    public int getMeta()
    {
        return this.meta;
    }

    public void setMeta(final int meta)
    {
        this.meta = meta;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (this.isClient())
            this.forceSync();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (this.blueprint != null && this.cachedBB == null)
        {
            this.cachedBB = new AxisAlignedBB(
                    this.pos.add(-this.blueprint.getMultiblock().getOffsetX(),
                            -this.blueprint.getMultiblock().getOffsetY(), -this.blueprint.getMultiblock().getOffsetZ()),
                    this.pos.add(this.blueprint.getMultiblock().getWidth(), this.blueprint.getMultiblock().getHeight(),
                            this.blueprint.getMultiblock().getLength()));
        }
        if (this.cachedBB != null)
            return this.cachedBB;
        return super.getRenderBoundingBox();
    }
}
