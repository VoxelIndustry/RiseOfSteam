package net.qbar.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.ITileWorkshop;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.ClientActionBuilder;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.List;

@Getter
public class TileBlueprintPrinter extends TileMultiblockInventoryBase implements IActionReceiver, ITileWorkshop
{
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Setter
    private int grid;

    public TileBlueprintPrinter()
    {
        super(QBarMachines.BLUEPRINT_PRINTER, 1);

        this.grid = -1;
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.getGrid());

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + this.getGridObject().getCables().size());
        }
        else
            lines.add("Errored grid!");
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
                .inventory(19, 112).hotbar(19, 170).addInventory()
                .tile(this).filterSlot(0, 19, 90, stack -> stack.getItem() == Items.PAPER).addInventory().create();
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
                    sender.answer().withBoolean("blueprint", true).send();
                }
                else
                    sender.getPlayer().addItemStackToInventory(blueprint);

                this.decrStackSize(0, 1);
            }
        }
        else if ("MACHINES_LOAD".equals(actionID) && this.hasGrid())
        {
            ClientActionBuilder builder = sender.answer();

            this.getGridObject().getMachines().forEach((machine, node) ->
                    builder.withLong(machine.name(), node.getBlockPos().toLong()));

            builder.send();
        }
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getCorePos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.world;
    }

    @Override
    public WorkshopMachine getType()
    {
        return WorkshopMachine.PRINTER;
    }

    @Override
    public void onChunkUnload()
    {
        this.disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.isServer() && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }
}