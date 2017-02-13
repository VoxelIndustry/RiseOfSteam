package net.qbar.common.grid;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.util.vector.Vector2f;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.qbar.common.steam.SteamTank;

public class BeltGrid extends CableGrid
{
    private final SteamTank  tank;

    private final Set<IBelt> inputs;

    private final float      beltSpeed;

    public BeltGrid(final int identifier, final float beltSpeed)
    {
        super(identifier);

        this.beltSpeed = beltSpeed;
        this.tank = new SteamTank(0, 32 * 4, 1.5f);

        this.inputs = new HashSet<>();
    }

    @Override
    public void tick()
    {
        super.tick();

        for (final ITileCable<BeltGrid> cable : this.getCables())
        {
            final IBelt belt = (IBelt) cable;

            boolean hasChanged = false;

            if (!belt.getItems().isEmpty())
            {
                final Iterator<ItemBelt> iterator = belt.getItems().iterator();

                while (iterator.hasNext())
                {
                    final ItemBelt item = iterator.next();
                    if (item.getPos().getY() < 1)
                    {
                        item.getPos().setY(item.getPos().getY() + this.beltSpeed / 10);
                        hasChanged = true;
                    }
                    else
                    {
                        if (belt.getConnected(belt.getFacing()) != null)
                        {
                            final IBelt forward = (IBelt) belt.getConnected(belt.getFacing());

                            forward.getItems().add(item);
                            item.getPos().setY(0);
                            forward.itemUpdate();
                            iterator.remove();
                            hasChanged = true;
                        }
                        else
                        {
                            InventoryHelper.spawnItemStack(belt.getWorld(), belt.getPos().getX(), belt.getPos().getY(),
                                    belt.getPos().getZ(), item.getStack());
                            iterator.remove();
                            hasChanged = true;
                        }
                    }
                }
            }
            if (hasChanged)
                belt.itemUpdate();
        }
    }

    @Override
    CableGrid copy(final int identifier)
    {
        return new BeltGrid(identifier, this.beltSpeed);
    }

    public SteamTank getTank()
    {
        return this.tank;
    }

    public int getSteamCapacity()
    {
        return this.getCables().size() * 32;
    }

    @Override
    public void addCable(@Nonnull final ITileCable cable)
    {
        super.addCable(cable);
        this.getTank().setCapacity(this.getSteamCapacity());
    }

    @Override
    public boolean removeCable(final ITileCable cable)
    {
        if (super.removeCable(cable))
        {
            this.getTank().setCapacity(this.getSteamCapacity());

            if (this.inputs.contains(cable))
                this.inputs.remove(cable);
            return true;
        }
        return false;
    }

    public void addInput(final IBelt input)
    {
        this.inputs.add(input);
    }

    public void removeInput(final IBelt input)
    {
        this.inputs.remove(input);
    }

    public boolean insert(final IBelt belt, final ItemStack stack, final boolean doInsert)
    {
        if (this.inputs.contains(belt) && !belt.isSlope())
        {
            boolean enoughSpace = true;
            for (final ItemBelt item : belt.getItems())
            {
                if (item.getPos().y < 13 / 32F)
                {
                    enoughSpace = false;
                    break;
                }
            }
            if (!enoughSpace)
                return false;
            if (doInsert)
            {
                belt.getItems().add(new ItemBelt(stack, new Vector2f(11f / 32f, 0)));
                belt.itemUpdate();
            }
        }
        return true;
    }

    public ItemStack extract(final ItemStack stack, final boolean doExtract)
    {
        return stack;
    }
}
