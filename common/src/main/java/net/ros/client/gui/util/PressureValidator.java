package net.ros.client.gui.util;

import net.voxelindustry.brokkgui.validation.BaseTextValidator;

public class PressureValidator extends BaseTextValidator
{
    public PressureValidator()
    {
        this.setMessage("This is not a valid pressure!");
    }

    @Override
    public boolean eval(String data)
    {
        return data.matches("(\\d|\\.)+");
    }
}
