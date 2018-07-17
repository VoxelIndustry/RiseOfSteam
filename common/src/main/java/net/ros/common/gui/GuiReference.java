package net.ros.common.gui;

import fr.ourten.teabeans.function.TriFunction;
import lombok.Data;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Data
public class GuiReference
{
    private TriFunction<EntityPlayer, World, BlockPos, Container> containerSupplier;
    private TriFunction<EntityPlayer, World, BlockPos, Gui>       guiSupplier;

    private int uniqueID;

    public GuiReference(TriFunction<EntityPlayer, World, BlockPos, Container> containerSupplier,
                        TriFunction<EntityPlayer, World, BlockPos, Gui> guiSupplier)
    {
        this.containerSupplier = containerSupplier;
        this.guiSupplier = guiSupplier;

        this.uniqueID = GuiManager.getNextID(this);
    }
}
