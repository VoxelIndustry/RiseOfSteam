package net.ros.common.compat.journeymap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.ros.common.ROSConstants;

import javax.annotation.Nonnull;

@ClientPlugin
public class ROSJMPlugin implements IClientPlugin
{
    @Override
    public void initialize(@Nonnull IClientAPI jmClientApi)
    {
    }

    @Override
    public String getModId()
    {
        return ROSConstants.MODID;
    }

    @Override
    public void onEvent(@Nonnull ClientEvent event)
    {

    }
}
