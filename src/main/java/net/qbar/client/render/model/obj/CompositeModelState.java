package net.qbar.client.render.model.obj;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class CompositeModelState implements IModelState
{
    private final IModelState first;
    private final IModelState second;

    public CompositeModelState(IModelState first, IModelState second)
    {
        this.first = first;
        this.second = second;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        Optional<TRSRTransformation> f = first.apply(part), s = second.apply(part);
        if (f.isPresent() && s.isPresent())
            return Optional.of(f.get().compose(s.get()));
        return f.or(s);
    }

    public IModelState getFirst()
    {
        return first;
    }

    public IModelState getSecond()
    {
        return second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        CompositeModelState that = (CompositeModelState) o;
        return Objects.equal(first, that.first) && Objects.equal(second, that.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(first, second);
    }
}