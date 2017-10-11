package net.qbar.common.network.action;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ActionPattern
{
    private Map<String, Class<?>> values;

    public ActionPattern()
    {
        this.values = new HashMap<>();
    }
}
