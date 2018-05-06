package net.ros.common.machine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.yggard.brokkgui.paint.Color;

@Getter
@AllArgsConstructor
public enum EMachineType
{
    ENERGY_PRODUCER(Color.fromHex("#CDDC39")),
    RESOURCE_PRODUCER(Color.fromHex("#4CAF50")),
    UTILITY(Color.fromHex("#9E9E9E")),
    CRAFTER(Color.fromHex("#9FA8DA")),
    LOGISTIC(Color.fromHex("#B39DDB"));

    Color color;
}
