package net.qbar.common.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemMultiblockBox extends ItemBase
{
    public ItemMultiblockBox()
    {
        super("multiblockbox");

        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer player, EnumHand hand)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(
                player.getHeldItem(hand).getTagCompound().getTagList("Items", 10).tagCount(), ItemStack.EMPTY);
        ItemUtils.loadAllItems(player.getHeldItem(hand).getTagCompound(), items);
        for (ItemStack item : items)
        {
            if (!player.addItemStackToInventory(item))
                InventoryHelper.spawnItemStack(w, player.posX, player.posY, player.posZ, item);
        }
        player.getHeldItem(hand).setCount(0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World w, List<String> tooltip, ITooltipFlag flag)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("blueprint"))
        {
            tooltip.add(ChatFormatting.GOLD
                    + I18n.format("tile." + stack.getTagCompound().getString("blueprint") + ".name"));
            tooltip.add(ChatFormatting.RED + "" + "Right click to open");
            tooltip.add("");

            NonNullList<ItemStack> items = NonNullList
                    .withSize(stack.getTagCompound().getTagList("Items", 10).tagCount(), ItemStack.EMPTY);
            ItemUtils.loadAllItems(stack.getTagCompound(), items);
            for (ItemStack item : items)
                tooltip.add(ItemUtils.getPrettyStackName(item));
        }
        super.addInformation(stack, w, tooltip, flag);
    }

    public ItemStack getBox(Blueprint blueprint)
    {
        ItemStack box = new ItemStack(this);
        box.setTagCompound(new NBTTagCompound());

        NonNullList<ItemStack> stacks = NonNullList.create();

        blueprint.getSteps().forEach(list -> list.forEach(stack ->
        {
            Optional<ItemStack> toMerge = stacks.stream().filter(stack2 -> ItemUtils.canMergeStacks(stack, stack2))
                    .findAny();

            if (toMerge.isPresent())
                toMerge.get().grow(stack.getCount());
            else
                stacks.add(stack.copy());
        }));

        ItemUtils.saveAllItems(box.getTagCompound(), stacks);
        box.getTagCompound().setString("blueprint", blueprint.getDescriptor().getName());
        return box;
    }
}
