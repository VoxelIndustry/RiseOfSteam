package net.qbar.debug.common.chip;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ChipData
{
    private Map<String, Long> counters = new LinkedHashMap<>();
}
