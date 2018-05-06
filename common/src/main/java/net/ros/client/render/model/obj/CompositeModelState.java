package net.ros.client.render.model.obj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
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
        return f.isPresent() ? f : s;
    }
}