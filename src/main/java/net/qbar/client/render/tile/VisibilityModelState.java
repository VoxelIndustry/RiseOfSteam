package net.qbar.client.render.tile;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;

public class VisibilityModelState implements IModelState
{
    public final List<String>                  hidden = new ArrayList<String>();
    private final Optional<TRSRTransformation> value  = Optional.of(TRSRTransformation.identity());

    @Override
    public Optional<TRSRTransformation> apply(final Optional<? extends IModelPart> part)
    {
        if (part.isPresent())
        {
            final UnmodifiableIterator<String> parts = Models.getParts(part.get());
            if (parts.hasNext())
            {
                final String name = parts.next();
                if (!parts.hasNext() && this.hidden.contains(name))
                    return this.value;
            }
        }
        return Optional.absent();
    }
}
