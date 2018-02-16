package net.qbar.common.tile.machine;

import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.machine.SteamComponent;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.ISteamHandlerItem;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.List;

public class TileCapsuleFiller extends TileMultiblockInventoryBase implements ITickable
{
    @Getter
    private final SteamTank      tank;
    private       SteamComponent steamComponent;

    public TileCapsuleFiller()
    {
        super(QBarMachines.CAPSULE_FILLER, 1);

        this.steamComponent = this.getDescriptor().get(SteamComponent.class);
        this.tank = new SteamTank(0, steamComponent.getSteamCapacity(), steamComponent.getMaxPressureCapacity());
    }

    @Override
    public void update()
    {
        if (this.isClient())
            return;

        if (!this.getStackInSlot(0).isEmpty())
        {
            ISteamHandlerItem item = this.getStackInSlot(0).getCapability(
                    CapabilitySteamHandler.ITEM_STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            if (item != null && this.getTank().getSteam() > 0 &&
                    item.getSteam() < item.getCapacity() * item.getMaxPressure())
                item.fillSteam(this.tank.drainSteam(this.steamComponent.getSteamConsumption(), true), true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        this.tank.writeToNBT(tag);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.tank.readFromNBT(tag);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.tank.getSteam() + " / " + this.tank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.tank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.tank.getMaxPressure()));
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return stack.hasCapability(CapabilitySteamHandler.ITEM_STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY.cast(this.getTank());
        return null;
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return new ContainerBuilder("capsule_filler", player)
                .player(player.inventory).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this)
                .steamSlot(0, 80, 36)
                .syncIntegerValue(this::getSteamAmount, this::setSteamAmount)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.CAPSULE_FILLER.getUniqueID(), this.getWorld(),
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    private int getSteamAmount()
    {
        return this.getTank().getSteam();
    }

    private void setSteamAmount(final int amount)
    {
        this.getTank().setSteam(amount);
    }
}
