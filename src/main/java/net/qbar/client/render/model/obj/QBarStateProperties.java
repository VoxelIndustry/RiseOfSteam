package net.qbar.client.render.model.obj;

import net.minecraftforge.common.property.IUnlistedProperty;

public class QBarStateProperties
{
    public static final IUnlistedProperty<QBarOBJState> VISIBILITY_PROPERTY = new IUnlistedProperty<QBarOBJState>()
    {
        public String getName()
        {
            return "qbar_visibility";
        }

        public boolean isValid(QBarOBJState state)
        {
            return true;
        }

        public Class<QBarOBJState> getType()
        {
            return QBarOBJState.class;
        }

        public String valueToString(QBarOBJState state)
        {
            return state.toString();
        }
    };
}
