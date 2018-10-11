package net.ros.common.machine.event;

import lombok.Getter;
import lombok.Setter;
import net.ros.common.machine.module.IModularMachine;
import net.ros.common.recipe.RecipeBase;
import net.voxelindustry.hermod.HermodEvent;

import javax.annotation.Nullable;

@Getter
public class RecipeChangeEvent extends HermodEvent
{
    @Nullable
    private final RecipeBase previousRecipe;
    @Nullable
    private final RecipeBase nextRecipe;

    @Setter
    private boolean cancelled;

    public RecipeChangeEvent(IModularMachine source, @Nullable RecipeBase previous,
                             @Nullable RecipeBase next)
    {
        super(source);

        this.previousRecipe = previous;
        this.nextRecipe = next;
    }
}
