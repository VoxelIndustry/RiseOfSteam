package net.ros.common;

import com.google.common.collect.Comparators;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.ros.common.block.BlockPipeBase;
import net.ros.common.block.BlockPipeCover;
import net.ros.common.grid.node.PipeType;
import net.ros.common.recipe.Materials;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.function.Supplier;

public class PipesCreativeTab extends CustomCreativeTab
{
    public PipesCreativeTab(String label, Supplier<Block> iconSupplier)
    {
        super(label, iconSupplier);
    }

    @Override
    public void displayAllRelevantItems(@Nonnull NonNullList<ItemStack> items)
    {
        super.displayAllRelevantItems(items);

        items.sort((firstItem, secondItem) ->
        {
            Block firstPipe = Block.getBlockFromItem(firstItem.getItem());
            Block secondPipe = Block.getBlockFromItem(secondItem.getItem());

            if (firstPipe == Blocks.AIR && secondPipe != Blocks.AIR)
                return -1;
            else if (secondPipe == Blocks.AIR && firstPipe != Blocks.AIR)
                return 1;
            else if (firstPipe == Blocks.AIR)
                return 0;

            if (firstPipe instanceof BlockPipeCover && !(secondPipe instanceof BlockPipeCover))
                return 1;
            else if (secondPipe instanceof BlockPipeCover && !(firstPipe instanceof BlockPipeCover))
                return -1;
            else if (firstPipe instanceof BlockPipeCover)
            {
                // Sort pipes covers
                if (((BlockPipeCover) firstPipe).getCoverType() != ((BlockPipeCover) secondPipe).getCoverType())
                    return ((BlockPipeCover) firstPipe).getCoverType().ordinal() - ((BlockPipeCover) secondPipe).getCoverType().ordinal();
            }

            // Sort pipes
            return comparePipeTypes(((BlockPipeBase) firstPipe).getPipeType(),
                    ((BlockPipeBase) secondPipe).getPipeType());
        });
    }

    private int comparePipeTypes(PipeType firstType, PipeType secondType)
    {
        if (firstType.getNature() != secondType.getNature())
            return firstType.getNature().ordinal() - secondType.getNature().ordinal();
        if (firstType.getMetal() != secondType.getMetal())
            return Materials.metals.indexOf(firstType.getMetal()) - Materials.metals.indexOf(secondType.getMetal());
        if (firstType.getSize() != secondType.getSize())
            return firstType.getSize().ordinal() - secondType.getSize().ordinal();
        return 0;
    }
}
