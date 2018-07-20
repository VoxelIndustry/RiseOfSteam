package net.ros.common.tile;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.grid.node.PipeType;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.steam.SteamTank;

import javax.annotation.Nullable;

public class TilePressureValve extends TileSteamPipe implements IContainerProvider, IActionReceiver
{
    @Getter
    @Setter
    private float fillPressureLimit;
    @Getter
    @Setter
    private float drainPressureLimit;

    public TilePressureValve(PipeType type)
    {
        super(type);

        this.fillPressureLimit = this.getMaxPressure();
        this.drainPressureLimit = 0;
    }

    public TilePressureValve()
    {
        this(null);
    }

    @Override
    protected SteamTank createSteamTank(int capacity, float maxPressure)
    {
        return new SteamTank(capacity, maxPressure)
        {
            private TilePressureValve valve;

            {
                this.valve = TilePressureValve.this;
            }

            @Override
            public int drainSteam(int amount, boolean doDrain)
            {
                if (valve.getDrainPressureLimit() != -1)
                {
                    if (this.getPressure() <= valve.getDrainPressureLimit())
                        return 0;
                    if ((this.getSteam() - amount / this.getCapacity()) < valve.getDrainPressureLimit())
                        return super.drainSteam(-this.getSteamDifference(valve.getDrainPressureLimit()), doDrain);
                }
                return super.drainSteam(amount, doDrain);
            }

            @Override
            public int fillSteam(int amount, boolean doFill)
            {
                if (valve.getFillPressureLimit() != -1)
                {
                    if (this.getPressure() >= valve.getFillPressureLimit())
                        return 0;
                    if (((this.getSteam() + amount) / this.getCapacity()) > valve.getFillPressureLimit())
                        return super.fillSteam(this.getSteamDifference(valve.getFillPressureLimit()), doFill);
                }
                return super.fillSteam(amount, doFill);
            }
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.fillPressureLimit = tag.getFloat("fillPressureLimit");
        this.drainPressureLimit = tag.getFloat("drainPressureLimit");

        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("fillPressureLimit", this.fillPressureLimit);
        tag.setFloat("drainPressureLimit", this.drainPressureLimit);

        return super.writeToNBT(tag);
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation("gui.steampressurevalve.name");
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("steampressurevalve", player)
                .player(player).inventory(8, 84).hotbar(8, 142)
                .addInventory()
                .syncFloatValue(this::getDrainPressureLimit, this::setDrainPressureLimit)
                .syncFloatValue(this::getFillPressureLimit, this::setFillPressureLimit)
                .syncIntegerValue(this.getBufferTank()::getSteam, this.getBufferTank()::setSteam)
                .create();
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("minpressure".equals(actionID))
            this.setDrainPressureLimit(payload.getFloat("pressure"));
        else if ("maxpressure".equals(actionID))
            this.setFillPressureLimit(payload.getFloat("pressure"));
    }
}
