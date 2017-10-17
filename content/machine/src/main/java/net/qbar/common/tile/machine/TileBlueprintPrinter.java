package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;

public class TileBlueprintPrinter extends TileMultiblockInventoryBase implements IActionReceiver
{
    public TileBlueprintPrinter()
    {
        super("blueprintprinter", 1);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index == 0 && stack.getItem() == Items.PAPER;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index == 0;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("blueprintprinter", player).player(player.inventory)
                .inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this).filterSlot(0, 0, 0, stack -> stack.getItem() == Items.PAPER).addInventory().create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        T rtn = this.getCapability(capability, BlockPos.ORIGIN, facing);
        return rtn != null ? rtn : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.getInventoryWrapper(facing);
        return null;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.BLUEPRINTPRINTER.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("PRINT".equals(actionID))
        {
            if (this.getStackInSlot(0).getCount() > 0 &&
                    QBarMachines.contains(Blueprint.class, payload.getString("blueprint")))
            {
                ItemStack blueprint = new ItemStack(QBarItems.BLUEPRINT);
                NBTTagCompound tag = new NBTTagCompound();
                blueprint.setTagCompound(tag);

                tag.setString("blueprint", payload.getString("blueprint"));

                if (sender.getPlayer().inventory.getItemStack().isEmpty())
                {
                    sender.getPlayer().inventory.setItemStack(blueprint);
                    sender.answer().send();
                }
                else
                    sender.getPlayer().addItemStackToInventory(blueprint);

                this.decrStackSize(0, 1);
            }
        }
    }
}