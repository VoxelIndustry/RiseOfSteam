package net.qbar.common.network.action;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class ActionManager
{
    private static ActionManager INSTANCE;

    public static ActionManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ActionManager();
        return INSTANCE;
    }

    private HashMap<Short, IActionCallback> callbackMap;

    private ActionManager()
    {
        this.callbackMap = new HashMap<>();
    }

    void addCallback(Short actionID, IActionCallback callback)
    {
        this.callbackMap.put(actionID, callback);
    }

    public void triggerCallback(Short actionID, NBTTagCompound payload)
    {
        if(!this.callbackMap.containsKey(actionID))
            return;
        this.callbackMap.get(actionID).call(payload);
        this.callbackMap.remove(actionID);
    }
}
