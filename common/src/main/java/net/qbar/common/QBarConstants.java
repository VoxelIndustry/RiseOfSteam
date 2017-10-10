package net.qbar.common;

import com.elytradev.concrete.network.NetworkContext;
import net.minecraft.creativetab.CreativeTabs;
import org.apache.logging.log4j.Logger;

public class QBarConstants
{
    public static final String MODID   = "qbar";
    public static final String MODNAME = "QBar";
    public static final String VERSION = "0.1.0";

    public static CreativeTabs TAB_ALL;
    public static Logger       LOGGER;

    public static Object MODINSTANCE;

    public static NetworkContext network;
}
