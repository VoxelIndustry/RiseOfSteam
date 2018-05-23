package net.ros.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import fr.ourten.teabeans.value.BaseProperty;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.card.CraftCard;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.grid.node.ITileWorkshop;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.multiblock.ITileMultiblockCore;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.ClientActionBuilder;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.tile.ILoadable;
import net.ros.common.tile.TileBase;
import net.ros.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TileEngineerWorkbench extends TileBase implements IContainerProvider, ITileMultiblockCore,
        ILoadable, ITileWorkshop, IActionReceiver
{
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Setter
    private       int                                         grid;

    private NonNullList<ItemStack> craftables;
    private int[]                  craftablesCount;
    private List<CraftCard>        recipes;

    private BaseProperty<Boolean> craftablesDirty;

    public TileEngineerWorkbench()
    {
        this.grid = -1;
        this.craftables = NonNullList.create();
        this.craftablesDirty = new BaseProperty<>(false);
    }

    public void refreshWorkbenchCrafts()
    {
        if (this.isClient())
            return;
        if (!this.hasGrid())
            return;
        if (!this.getGridObject().getMachines().containsKey(WorkshopMachine.CARDLIBRARY))
        {
            if (!this.craftables.isEmpty())
            {
                this.craftables.clear();
                this.sync();
            }
            return;
        }

        this.craftables.clear();
        this.recipes = new ArrayList<>();
        ((TileModularMachine) this.getGridObject().getMachines().get(WorkshopMachine.CARDLIBRARY))
                .getModule(InventoryModule.class).getInventory("basic")
                .getStacks().stream().filter(card -> !card.isEmpty())
                .map(card -> CardDataStorage.instance().read(card.getTagCompound(), CraftCard.class))
                .forEach(card ->
                {
                    if (!recipes.contains(card))
                        recipes.add(card);
                });

        this.craftables.addAll(recipes.stream().map(card ->
        {
            ItemStack result = card.getResult().copy();
            result.setCount(1);
            return result;
        }).collect(Collectors.toList()));
        this.craftablesCount = new int[this.craftables.size()];

        if (this.getGridObject().getMachines().containsKey(WorkshopMachine.STORAGE))
        {
            InventoryHandler storage = ((TileModularMachine) this.getGridObject().getMachines()
                    .get(WorkshopMachine.STORAGE)).getModule(InventoryModule.class).getInventory("basic");

            this.craftables.stream().parallel().forEach(result ->
            {
                int count = Integer.MAX_VALUE;

                List<ItemStack> relevantStacks = Lists.newArrayList(storage.getStacks());
                relevantStacks.removeIf(ItemStack::isEmpty);

                for (ItemStack ingredient : recipes.get(this.craftables.indexOf(result)).getCompressedRecipe())
                {
                    List<ItemStack> ingredientCandidate = relevantStacks.stream()
                            .filter(stack -> ItemUtils.deepEquals(stack, ingredient)).collect(Collectors.toList());

                    if (!ingredientCandidate.isEmpty())
                        count = Math.min(count, ingredientCandidate.stream()
                                .mapToInt(ItemStack::getCount).sum() / ingredient.getCount());
                    else
                    {
                        count = 0;
                        break;
                    }
                }
                if (count == Integer.MAX_VALUE)
                    count = 0;
                craftablesCount[this.craftables.indexOf(result)] = count *
                        recipes.get(this.craftables.indexOf(result)).getResult().getCount();
            });
        }
        this.sync();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (this.isClient())
        {
            this.craftables.clear();

            this.craftablesCount = new int[tag.getInteger("itemCount")];
            for (int i = 0; i < tag.getInteger("itemCount"); i++)
            {
                this.craftables.add(new ItemStack(tag.getCompoundTag("item" + i)));
                this.craftablesCount[i] = tag.getInteger("itemCount" + i);
            }

            this.craftablesDirty.setValue(true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        if (this.isServer())
        {
            int index = 0;
            for (ItemStack stack : this.craftables)
            {
                tag.setTag("item" + index, stack.writeToNBT(new NBTTagCompound()));
                tag.setInteger("itemCount" + index, this.craftablesCount[index]);
                index++;
            }
            tag.setInteger("itemCount", index);
        }

        return super.writeToNBT(tag);
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
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("engineerworkbench", player)
                .player(player).inventory(19, 102).hotbar(19, 160)
                .addInventory().create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.ENGINEERWORKBENCH.getUniqueID(), this.world, this.pos
                        .getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getPos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.world;
    }

    @Override
    public WorkshopMachine getType()
    {
        return WorkshopMachine.WORKBENCH;
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

    private boolean canCraft(EntityPlayer player, ItemStack result, int toMake)
    {
        if (player.inventory.getItemStack().isEmpty())
            return true;

        if (ItemUtils.deepEquals(player.inventory.getItemStack(), result) &&
                player.inventory.getItemStack().getCount() + toMake <= result.getMaxStackSize())
            return true;
        return false;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("MACHINES_LOAD".equals(actionID) && this.hasGrid())
        {
            ClientActionBuilder builder = sender.answer();

            this.getGridObject().getMachines().forEach((machine, node) ->
                    builder.withLong(machine.name(), node.getBlockPos().toLong()));

            builder.send();
        }
        else if ("CRAFT_ITEM".equals(actionID))
        {
            int index = payload.getInteger("index");

            if (index >= 0 && index < this.craftables.size())
            {
                CraftCard recipe = this.recipes.get(index);
                int available = this.craftablesCount[index];

                int toMake = payload.getBoolean("stack") ?
                        Math.min(available, (64 / recipe.getResult().getCount()) * recipe.getResult().getCount()) :
                        recipe.getResult().getCount();

                if (!payload.getBoolean("stack") && !this.canCraft(sender.getPlayer(), recipe.getResult(), toMake))
                    return;

                for (ItemStack ingredient : recipe.getCompressedRecipe())
                {
                    InventoryHandler storage = ((TileModularMachine) this.getGridObject().getMachines()
                            .get(WorkshopMachine.STORAGE)).getModule(InventoryModule.class).getInventory("basic");

                    int toConsume = ingredient.getCount() * (toMake / recipe.getResult().getCount());
                    for (ItemStack stack : storage.getStacks())
                    {
                        if (stack.isEmpty() || !ItemUtils.deepEquals(stack, ingredient))
                            continue;
                        if (stack.getCount() >= toConsume)
                        {
                            stack.shrink(toConsume);
                            toConsume = 0;
                            break;
                        }
                        else
                        {
                            toConsume -= stack.getCount();
                            stack.setCount(0);
                        }
                    }

                    if (toConsume > 0)
                        return;
                }
                this.refreshWorkbenchCrafts();

                ItemStack produced = recipe.getResult().copy();
                produced.setCount(toMake);

                if (payload.getBoolean("stack"))
                    sender.getPlayer().addItemStackToInventory(produced);
                else
                {
                    if (sender.getPlayer().inventory.getItemStack().isEmpty())
                        sender.getPlayer().inventory.setItemStack(produced);
                    else
                        sender.getPlayer().inventory.getItemStack().grow(toMake);
                }
            }
            sender.answer().withItemStack("cursor", sender.getPlayer().inventory.getItemStack()).send();
            this.sync();
        }
    }
}
