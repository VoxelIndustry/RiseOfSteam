package net.qbar.client.render.model.obj;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class QBarOBJState implements IModelState
{
    protected List<String> visibilityList;
    protected boolean      whitelist;
    public IModelState     parent;

    public QBarOBJState(List<String> visibleGroups, boolean visibility)
    {
        this(visibleGroups, visibility, TRSRTransformation.identity());
    }

    public QBarOBJState(List<String> visibleGroups, boolean visibility, IModelState parent)
    {
        this.parent = parent;

        this.visibilityList = Lists.newArrayList(visibleGroups);
        this.whitelist = visibility;
    }

    @Nullable
    public IModelState getParent(IModelState parent)
    {
        if (parent == null)
            return null;
        else if (parent instanceof QBarOBJState)
            return ((QBarOBJState) parent).parent;
        return parent;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if (parent != null)
            return parent.apply(part);
        return Optional.absent();
    }

    public List<String> getVisibilityList()
    {
        return visibilityList;
    }

    public boolean isWhitelist()
    {
        return whitelist;
    }
}
