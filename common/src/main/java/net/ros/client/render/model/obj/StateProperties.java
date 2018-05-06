package net.ros.client.render.model.obj;

import net.minecraftforge.common.property.IUnlistedProperty;

public class StateProperties
{
    public static final IUnlistedProperty<ROSOBJState> VISIBILITY_PROPERTY = new IUnlistedProperty<ROSOBJState>()
    {
        public String getName()
        {
            return "ros_visibility";
        }

        public boolean isValid(ROSOBJState state)
        {
            return true;
        }

        public Class<ROSOBJState> getType()
        {
            return ROSOBJState.class;
        }

        public String valueToString(ROSOBJState state)
        {
            return state.toString();
        }
    };
}
