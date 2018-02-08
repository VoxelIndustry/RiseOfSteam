package net.qbar.common.grid;

import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.qbar.common.steam.SteamTank;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;
import java.util.Iterator;

public class BeltGrid extends CableGrid
{
    private final SteamTank tank;

    @Getter
    private final float     beltSpeed;

    private final float     BELT_MIDDLE      = 10 / 32F;

    private int             movedCount       = 0;

    private boolean         lastWorkingState = false;

    public BeltGrid(final int identifier, final float beltSpeed)
    {
        super(identifier);

        this.beltSpeed = beltSpeed;
        this.tank = new SteamTank(0, 64 * 4, 1.5f);
    }

    @Override
    CableGrid copy(final int identifier)
    {
        return new BeltGrid(identifier, this.beltSpeed);
    }

    @Override
    boolean canMerge(final CableGrid grid)
    {
        if (grid instanceof BeltGrid && ((BeltGrid) grid).getBeltSpeed() == this.beltSpeed)
            return super.canMerge(grid);
        return false;
    }

    @Override
    void onMerge(final CableGrid grid)
    {
        this.getTank().setCapacity(this.getSteamCapacity());
        if (((BeltGrid) grid).getTank().getSteam() != 0)
            this.getTank().fillInternal(((BeltGrid) grid).getTank().getSteam(), true);
        if (this.lastWorkingState != ((BeltGrid) grid).getLastWorkingState())
        {
            grid.getCables().forEach(cable ->
            {
                ((IBelt) cable).setWorking(this.lastWorkingState);
                ((IBelt) cable).itemUpdate();
            });
        }
    }

    @Override
    void onSplit(final CableGrid grid)
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
        {
            final IBelt belt = (IBelt) cable;

            boolean hasChanged = false;

            if (this.getTank().getSteam() > 0)
            {
                if (!belt.isWorking() && System.currentTimeMillis() - belt.getLastWorkStateChange() > 500)
                {
                    belt.setWorking(true);
                    belt.setChanged(true);
                }
                if (!belt.getItems().isEmpty())
                {
                    final Iterator<ItemBelt> iterator = belt.getItems().iterator();

                    while (iterator.hasNext())
                    {
                        final ItemBelt item = iterator.next();

                        if (item.getPos().getX() > this.BELT_MIDDLE)
                        {
                            item.getPos().setX(item.getPos().getX()
                                    - Math.min(item.getPos().getX() - this.BELT_MIDDLE, this.beltSpeed / 3));
                            hasChanged = true;
                        }
                        else if (item.getPos().getX() < this.BELT_MIDDLE)
                        {
                            item.getPos().setX(item.getPos().getX()
                                    + Math.min(this.BELT_MIDDLE - item.getPos().getX(), this.beltSpeed / 3));
                            hasChanged = true;
                        }

                        if (item.getPos().getY() < 1)
                        {
                            if (!this.checkCollision(belt, item, this.beltSpeed / 3))
                            {
                                item.getPos().setY(item.getPos().getY() + this.beltSpeed / 3);
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
                                    forward.getItems().add(item);

                                    if (belt.getFacing() == forward.getFacing())
                                        item.getPos().setY(0);
                                    else if (belt.getFacing().rotateY() == forward.getFacing())
                                    {
                                        item.getPos().setX(10 / 16F);
                                        item.getPos().setY(this.BELT_MIDDLE);
                                    }
                                    else
                                    {
                                        item.getPos().setX(0);
                                        item.getPos().setY(this.BELT_MIDDLE);
                                    }
                                    forward.setChanged(true);
                                    iterator.remove();
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
                                            && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                    belt.getFacing().getOpposite()))
                                    {
                                        if (ItemHandlerHelper
                                                .insertItem(tile.getCapability(
                                                        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                        belt.getFacing().getOpposite()), item.getStack(), true)
                                                .isEmpty())
                                        {
                                            ItemHandlerHelper.insertItem(
                                                    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                            belt.getFacing().getOpposite()),
                                                    item.getStack(), false);
                                            iterator.remove();
                                            hasChanged = true;
                                        }
                                    }
                                    else
                                    {
                                        InventoryHelper.spawnItemStack(belt.getBlockWorld(), belt.getBlockPos().getX(),
                                                belt.getBlockPos().getY(), belt.getBlockPos().getZ(), item.getStack());
                                        iterator.remove();
                                        hasChanged = true;
                                    }
                                }
                                else
                                {
                                    final TileEntity tile = belt.getBlockWorld().getTileEntity(upward);

                                    if (tile != null
                                            && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                    belt.getFacing().getOpposite()))
                                    {
                                        if (ItemHandlerHelper
                                                .insertItem(tile.getCapability(
                                                        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                        belt.getFacing().getOpposite()), item.getStack(), true)
                                                .isEmpty())
                                        {
                                            ItemHandlerHelper.insertItem(
                                                    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                                            belt.getFacing().getOpposite()),
                                                    item.getStack(), false);
                                            iterator.remove();
                                            hasChanged = true;
                                        }
                                    }
                                }
                            }
                        }
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

    public SteamTank getTank()
    {
        return this.tank;
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

    public boolean insert(final IBelt belt, final ItemStack stack, final boolean doInsert)
    {
        if (!belt.isSlope())
        {
            boolean enoughSpace = true;
            for (final ItemBelt item : belt.getItems())
            {
                if (!item.getStack().isEmpty() && item.getPos().y < 13 / 32F)
                {
                    enoughSpace = false;
                    break;
                }
            }
            if (!enoughSpace)
                return false;
            if (doInsert && !stack.isEmpty())
            {
                belt.getItems().add(new ItemBelt(stack, new Vector2f(this.BELT_MIDDLE, 0)));
                belt.itemUpdate();
            }
        }
        return true;
    }

    private boolean checkCollision(final IBelt belt, final ItemBelt item, final float add)
    {
        if (!belt.getItems().isEmpty())
        {
            for (final ItemBelt collidable : belt.getItems())
            {
                if (collidable != item)
                {
                    if (collidable.getPos().getY() < item.getPos().getY() + add + 6 / 16F
                            && collidable.getPos().getY() > item.getPos().getY() + add)
                        return true;
                }
            }
        }
        if (item.getPos().getY() + add > 10 / 16F)
        {
            if (belt.getConnected(belt.getFacing()) != null)
            {
                final IBelt forward = (IBelt) belt.getConnected(belt.getFacing());

                if (forward.getFacing() == belt.getFacing().getOpposite())
                    return true;
                if (!forward.getItems().isEmpty())
                {
                    for (final ItemBelt collidable : forward.getItems())
                    {
                        if (belt.getFacing() == forward.getFacing())
                        {
                            if (collidable.getPos().getY() < item.getPos().getY() + add - 10 / 16F
                                    && collidable.getPos().getY() > item.getPos().getY() + add - 16 / 16F)
                                return true;
                        }
                        else if (item.getPos().getY() + add > 14 / 16F)
                        {
                            if (collidable.getPos().getY() < 10 / 16F && collidable.getPos().getY() > 0F)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean getLastWorkingState()
    {
        return this.lastWorkingState;
    }
}
