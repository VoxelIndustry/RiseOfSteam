package net.qbar.common.grid;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

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

        this.inputs.forEach(input -> input.extractItems());
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
}
