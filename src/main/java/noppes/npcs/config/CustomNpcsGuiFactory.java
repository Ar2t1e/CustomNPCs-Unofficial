package noppes.npcs.config;

import net.minecraftforge.client.ConfigScreenHandler;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.config.CustomNpcsConfigGui;

import java.util.function.Supplier;

public class CustomNpcsGuiFactory {

    public static final Supplier<ConfigScreenHandler.ConfigScreenFactory> FACTORY = () -> new ConfigScreenHandler.ConfigScreenFactory((parentScreen) -> new CustomNpcsConfigGui(parentScreen, CustomNpcs.Config.getChildElements(), CustomNpcs.MODNAME));

}
