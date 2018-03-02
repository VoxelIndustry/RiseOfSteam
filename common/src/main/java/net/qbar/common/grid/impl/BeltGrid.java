package net.qbar.common.grid.impl;

import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.qbar.common.grid.ItemBelt;
import net.qbar.common.grid.node.IBelt;
import net.qbar.common.grid.node.ITileNode;
import net.qbar.common.steam.SteamTank;

import javax.annotation.Nonnull;

public class BeltGrid extends CableGrid
{
    @Getter
    private final SteamTank tank;

    @Getter
    private final float beltSpeed;
    private final float   BELT_MIDDLE      = 10 / 32F;
    private       int     movedCount       = 0;
    private       boolean lastWorkingState = false;

    private final Capability<IItemHandler> capability = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

    public BeltGrid(final int identifier, final float beltSpeed)
    {
        super(identifier);

        this.beltSpeed = beltSpeed;
        this.tank = new SteamTank(0, 64 * 4, 1.5f);
    }

    @Override
    public CableGrid copy(final int identifier)
    {
        return new BeltGrid(identifier, this.beltSpeed);
    }

    @Override
    public boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof BeltGrid && ((BeltGrid) grid).getBeltSpeed() == this.beltSpeed)
            return super.canMerge(grid);
        return false;
    }

    @Override
    public void onMerge(final CableGrid grid)
    {
        this.getTank().setCapacity(this.getSteamCapacity());
        if (((BeltGrid) grid).getTank().getSteam() != 0)
            this.getTank().fillInternal(((BeltGrid) grid).getTank().getSteam(), true);
        if (this.lastWorkingState != ((BeltGrid) grid).lastWorkingState)
        {
            grid.getCables().forEach(cable ->
            {
                ((IBelt) cable).setWorking(this.lastWorkingState);
                ((IBelt) cable).itemUpdate();
            });
        }
    }

    @Override
    public void onSplit(final CableGrid grid)
    {
        this.getTank()
                .fillInternal(((BeltGrid) grid).getTank().drainInternal(
                        ((BeltGrid) grid).getTank().getSteam() / grid.getCables().size() * this.getCables().size(),
                        false), true);
    }

    @Override
    public void tick()
    {
        super.tick();

        final boolean currentWorkingState = this.getTank().getSteam() > 0;

        for (final ITileNode<?> cable : this.getCables())
            this.updateBelt((IBelt) cable);

        for (final ITileNode<?> cable : this.getCables())
        {
            final IBelt belt = (IBelt) cable;

            if (belt.hasChanged())
            {
                belt.itemUpdate();
                belt.setChanged(false);
            }
        }
        this.lastWorkingState = currentWorkingState;
    }

    private void updateBelt(IBelt belt)
    {
        boolean hasChanged = false;

        if (!belt.isEmpty())
        {
            for (ItemBelt item : belt.getItems())
            {
                if (item == null)
                    continue;
                if (item.getPosX() != item.getPrevPosX() || item.getPosY() != item.getPrevPosY())
                {
                    item.setPrevPosX(item.getPosX());
                    item.setPrevPosY(item.getPosY());
                    hasChanged = true;
                }
            }
        }

        if (this.getTank().getSteam() > 0)
        {
            if (!belt.isWorking() && System.currentTimeMillis() - belt.getLastWorkStateChange() > 500)
            {
                belt.setWorking(true);
                belt.setChanged(true);
            }
            if (!belt.isEmpty())
            {
                for (int i = 0; i < belt.getItems().length; i++)
                {
                    if (belt.getItems()[i] == null)
                        continue;
                    if (this.updateItem(belt, i))
                        hasChanged = true;
                }
            }
            if (hasChanged)
            {
                if (!belt.hasChanged())
                {
                    belt.setChanged(true);
                    if (this.movedCount == 40)
                    {
                        this.getTank().drainSteam(1, true);
                        this.movedCount = 0;
                    }
                    else
                        this.movedCount++;
                }
            }
        }
        else if (belt.isWorking() && System.currentTimeMillis() - belt.getLastWorkStateChange() > 500)
        {
            belt.setWorking(false);
            belt.setChanged(true);
        }
    }

    private boolean updateItem(IBelt belt, int index)
    {
        boolean hasChanged = false;
        ItemBelt item = belt.getItems()[index];

        if (item.getPosX() > this.BELT_MIDDLE)
        {
            item.setPosX(item.getPosX()
                    - Math.min(item.getPosX() - this.BELT_MIDDLE, this.beltSpeed / 3));
            hasChanged = true;
        }
        else if (item.getPosX() < this.BELT_MIDDLE)
        {
            item.setPosX(item.getPosX()
                    + Math.min(this.BELT_MIDDLE - item.getPosX(), this.beltSpeed / 3));
            hasChanged = true;
        }

        if (item.getPosY() < 1)
        {
            if (!this.doCollide(belt, item, this.beltSpeed / 3))
            {
                item.setPosY(item.getPosY() + this.beltSpeed / 3);
                hasChanged = true;
            }
        }
        else
        {
            if (belt.getConnected(belt.getFacing()) != null)
            {
                final IBelt forward = (IBelt) belt.getConnected(belt.getFacing());

                if (belt.getFacing().getOpposite() != forward.getFacing())
                {
                    forward.addItem(item);

                    if (belt.getFacing() == forward.getFacing())
                        item.setPosY(0);
                    else if (belt.getFacing().rotateY() == forward.getFacing())
                    {
                        item.setPosX(10 / 16F);
                        item.setPosY(this.BELT_MIDDLE);
                    }
                    else
                    {
                        item.setPosX(0);
                        item.setPosY(this.BELT_MIDDLE);
                    }
                    forward.setChanged(true);
                    belt.getItems()[index] = null;
                    hasChanged = true;
                }
            }
            else
            {
                final BlockPos forward = belt.getBlockPos().offset(belt.getFacing());
                final BlockPos upward = forward.up();
                if (belt.getBlockWorld().getBlockState(upward).getMaterial() == Material.AIR)
                {
                    final TileEntity tile = belt.getBlockWorld().getTileEntity(forward);

                    if (tile != null
                            && tile.hasCapability(capability, belt.getFacing().getOpposite()))
                    {
                        if (ItemHandlerHelper.insertItem(tile.getCapability(capability,
                                belt.getFacing().getOpposite()), item.getStack(), true).isEmpty())
                        {
                            ItemHandlerHelper.insertItem(tile.getCapability(capability, belt.getFacing().getOpposite()),
                                    item.getStack(), false);
                            belt.getItems()[index] = null;
                            hasChanged = true;
                        }
                    }
                    else
                    {
                        InventoryHelper.spawnItemStack(belt.getBlockWorld(), belt.getBlockPos().getX(),
                                belt.getBlockPos().getY(), belt.getBlockPos().getZ(), item.getStack());
                        belt.getItems()[index] = null;
                        hasChanged = true;
                    }
                }
                else
                {
                    final TileEntity tile = belt.getBlockWorld().getTileEntity(upward);

                    if (tile != null && tile.hasCapability(capability, belt.getFacing().getOpposite()))
                    {
                        if (ItemHandlerHelper.insertItem(tile.getCapability(capability,
                                belt.getFacing().getOpposite()), item.getStack(), true).isEmpty())
                        {
                            ItemHandlerHelper.insertItem(tile.getCapability(capability, belt.getFacing().getOpposite()),
                                    item.getStack(), false);
                            belt.getItems()[index] = null;
                            hasChanged = true;
                        }
                    }
                }
            }
        }
        return hasChanged;
    }

    public int getSteamCapacity()
    {
        if (this.getCables().size() < 4)
            return 256;
        return this.getCables().size() * 64;
    }

    @Override
    public void addCable(@Nonnull final ITileNode cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getSteamCapacity());

        ((IBelt) cable).setWorking(this.lastWorkingState);
        ((IBelt) cable).itemUpdate();
    }

    @Override
    public boolean removeCable(final ITileNode cable)
    {
        if (super.removeCable(cable))
        {
            this.getTank().setCapacity(this.getSteamCapacity());
            return true;
        }
        return false;
    }

    public boolean insert(IBelt belt, ItemStack stack, float posX, float posY, boolean doInsert)
    {
        if (!belt.isSlope())
        {
            if (this.doCollide(belt, new ItemBelt(stack, posX, posY), this.beltSpeed / 3))
                return false;
            if (doInsert && !stack.isEmpty())
            {
                belt.addItem(new ItemBelt(stack, posX, posY));
                belt.itemUpdate();
            }
        }
        return true;
    }

    private boolean doCollide(final IBelt belt, final ItemBelt item, final float add)
    {
        if (!belt.isEmpty())
        {
            for (final ItemBelt collidable : belt.getItems())
            {
                if (collidable == null)
                    continue;
                if (collidable != item)
                {
                    if (collidable.getPosY() < item.getPosY() + add + 6 / 16F
                            && collidable.getPosY() > item.getPosY() - add)
                        return true;
                }
            }
        }
        if (item.getPosY() + add > 10 / 16F)
        {
            if (belt.getConnected(belt.getFacing()) != null)
            {
                final IBelt forward = (IBelt) belt.getConnected(belt.getFacing());

                if (forward.getFacing() == belt.getFacing().getOpposite())
                    return true;
                if (!forward.isEmpty())
                {
                    for (final ItemBelt collidable : forward.getItems())
                    {
                        if (collidable == null)
                            continue;
                        if (belt.getFacing() == forward.getFacing())
                        {
                            if (collidable.getPosY() < item.getPosY() + add - 10 / 16F
                                    && collidable.getPosY() > item.getPosY() + add - 16 / 16F)
                                return true;
                        }
                        else if (item.getPosY() + add > 14 / 16F)
                        {
                            if (collidable.getPosY() < 10 / 16F && collidable.getPosY() > 0F)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
