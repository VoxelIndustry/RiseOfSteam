package net.qbar.common.machine.event;

import lombok.Getter;
import lombok.Setter;
import net.qbar.common.machine.module.IModularMachine;
import net.qbar.common.recipe.QBarRecipe;
import org.yggard.hermod.HermodEvent;

import javax.annotation.Nullable;

@Getter
public class RecipeChangeEvent extends HermodEvent
{
    @Nullable
    private final QBarRecipe previousRecipe;
    @Nullable
    private final QBarRecipe nextRecipe;

    @Setter
    private boolean cancelled;

    public RecipeChangeEvent(IModularMachine source, @Nullable QBarRecipe previous,
                             @Nullable QBarRecipe next)
    {
        super(source);

        this.previousRecipe = previous;
        this.nextRecipe = next;
    }
}
