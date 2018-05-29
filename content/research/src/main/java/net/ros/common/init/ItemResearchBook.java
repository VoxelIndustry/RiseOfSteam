package net.ros.common.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.ros.client.gui.GuiBook;
import net.ros.common.ROSConstants;
import net.ros.common.gui.ResearchGui;
import net.ros.common.item.ItemBase;
import org.yggard.brokkgui.wrapper.impl.BrokkGuiManager;

public class ItemResearchBook extends ItemBase {
    public ItemResearchBook() {
        super("researchbook");

        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        BrokkGuiManager.openBrokkGuiScreen(new GuiBook());

       // playerIn.openGui(ROSConstants.MODINSTANCE, ResearchGui.RESEARCH_BOOK.getUniqueID(), worldIn, 0, 0, 0);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
