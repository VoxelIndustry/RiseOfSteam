package net.qbar.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.client.render.tile.RenderStructure;
import net.qbar.client.render.tile.VisibilityModelState;
import net.qbar.common.QBarConstants;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.multiblock.blueprint.BlueprintState;
import net.qbar.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class TileStructure extends QBarTileBase implements ITileMultiblockCore
{
    private Blueprint           blueprint;
    private MultiblockComponent multiblock;
    private BlueprintState      blueprintState;

    private boolean complete;

    @Getter
    @Setter
    private int meta;

    private AxisAlignedBB cachedBB;

    public final VisibilityModelState state = new VisibilityModelState();

    public TileStructure()
    {
    }

    public void stepBuilding(final EntityPlayer player)
    {
        if (this.getBlueprintState().needStack())
        {
            int i = 0;
            for (final ItemStack stack : this.getBlueprintState().getCurrentStacks())
            {
                if (stack.getCount() - 1 < this.getBlueprintState().getStepStacks().get(i).getCount())
                {
                    final ItemStack needed = stack.copy();
                    needed.setCount(
                            this.getBlueprintState().getStepStacks().get(i).getCount() - (stack.getCount() - 1));

                    if (!player.capabilities.isCreativeMode)
                        needed.setCount(ItemUtils.drainPlayer(player.inventory, needed));
                    if (needed.getCount() != 0)
                    {
                        this.getBlueprintState().addStack(needed);
                        this.sync();
                    }

                }
                if (!this.getBlueprintState().needStack())
                {
                    this.getBlueprintState().setStepStackComplete();
                    this.sync();
                    break;
                }
                i++;
            }
        }
        else
        {
            this.getBlueprintState().setCurrentTime(this.getBlueprintState().getCurrentTime() + 1);
            if (this.getBlueprintState().getCurrentTime() > this.getBlueprintState().getStepTime())
            {
                if (this.getBlueprintState()
                        .getCurrentStep() < this.getBlueprintState().getBlueprint().getSteps().size() - 1)
                    this.getBlueprintState().setCurrentStep(this.getBlueprintState().getCurrentStep() + 1);
                else
                {
                    final BlockMultiblockBase block = (BlockMultiblockBase) Block
                            .getBlockFromName("qbar:" + this.blueprint.getDescriptor().getName());
                    final IBlockState state = block.getStateFromMeta(this.meta);
                    final IBlockState previous = this.world.getBlockState(this.getPos());

                    this.complete = true;
                    this.world.setBlockToAir(this.pos);
                    this.world.notifyBlockUpdate(this.pos, previous, Blocks.AIR.getDefaultState(), 3);

                    this.world.setBlockState(this.pos, state);
                    block.onBlockPlacedBy(this.world, this.pos, state, null, ItemStack.EMPTY);

                    this.world.notifyBlockUpdate(this.pos, previous, state, 3);
                    return;
                }
            }
            this.sync();
        }
        this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        if (this.blueprint != null)
        {
            tag.setString("blueprint", this.blueprint.getDescriptor().getName());

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

        this.blueprint = QBarMachines.getComponent(Blueprint.class, tag.getString("blueprint"));
        this.multiblock = this.blueprint.getDescriptor().get(MultiblockComponent.class);
        if (this.blueprint != null)
            this.blueprintState = new BlueprintState(this.blueprint, tag.getCompoundTag("blueprintState"));
        this.meta = tag.getInteger("statemeta");
    }

    public void setBlueprint(final Blueprint blueprint)
    {
        this.blueprint = blueprint;
        this.multiblock = this.blueprint.getDescriptor().get(MultiblockComponent.class);
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

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), false);

        if (!this.complete)
        {
            this.blueprintState.getCurrentStacks().forEach(stack ->
            {
                stack.shrink(1);
                InventoryHelper.spawnItemStack(this.world, this.getPos().getX(), this.getPos().getY(), this.getPos()
                                .getZ(),
                        stack);
            });
            for (int i = 0; i < this.blueprint.getSteps().size(); i++)
            {
                if (i < this.blueprintState.getCurrentStep())
                    this.blueprint.getSteps().get(i).forEach(stack ->
                            InventoryHelper.spawnItemStack(this.world, this.getPos().getX(), this.getPos().getY(),
                                    this.getPos().getZ(), stack));
            }
        }
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
            if (BlockMultiblockBase.getFacing(this.meta).getAxis() == Axis.Z)
            {
                this.cachedBB = new AxisAlignedBB(
                        this.pos.add(-this.multiblock.getOffsetX(), -this.multiblock.getOffsetY(),
                                -this.multiblock.getOffsetZ()),
                        this.pos.add(this.multiblock.getWidth(), this.multiblock.getHeight(),
                                this.multiblock.getLength()));
            }
            else
                this.cachedBB = new AxisAlignedBB(
                        this.pos.add(-this.multiblock.getOffsetZ(), -this.multiblock.getOffsetY(),
                                -this.multiblock.getOffsetX()),
                        this.pos.add(this.multiblock.getLength(), this.multiblock.getHeight(),
                                this.multiblock.getWidth()));
        }
        if (this.cachedBB != null)
            return this.cachedBB;
        return super.getRenderBoundingBox();
    }

    private int previousStep = -1;
    private List<BakedQuad> quadsCache;

    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getQuads()
    {
        if (this.quadsCache == null || this.previousStep != this.getBlueprintState().getCurrentStep())
        {
            final IBlockState state = Block
                    .getBlockFromName(QBarConstants.MODID + ":" + this.getBlueprint().getDescriptor().getName())
                    .getStateFromMeta(this.getMeta());

            final IBakedModel model = RenderStructure.blockRender.getModelForState(state);

            if (this.getBlueprintState().getMultiblockStep() != null)
                this.quadsCache = model.getQuads(((BlockMultiblockBase) state.getBlock()).getGhostState(state,
                        this.getBlueprintState().getMultiblockStep().getAlphaState()), null, 0);
            else
                this.quadsCache = new ArrayList<>(0);

            this.previousStep = this.getBlueprintState().getCurrentStep();
        }
        return this.quadsCache;
    }
}
