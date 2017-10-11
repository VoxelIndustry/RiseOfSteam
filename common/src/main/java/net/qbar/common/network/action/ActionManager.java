package net.qbar.common.network.action;

import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ActionManager
{
    private static ActionManager INSTANCE;

    public static ActionManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ActionManager();
        return INSTANCE;
    }

    private Map<String, Pair<ActionTargetType, ActionPattern>> actionTypes;

    private ActionManager()
    {
        this.actionTypes = new HashMap<>();
    }

    public void registerAction(String actionKey, ActionTargetType target, ActionPattern pattern)
    {
        this.actionTypes.put(actionKey, Pair.of(target, pattern));
    }

    public ActionBuilder dispatchAction(String actionKey)
    {
        return new ActionBuilder(actionKey);
    }
}
