package noppes.npcs.client.gui;

import net.minecraft.network.chat.Component;
import noppes.npcs.containers.ContainerBuilderSettings;
import noppes.npcs.shared.client.gui.GuiBasicContainer;

public class GuiBuilderTools extends GuiBasicContainer<ContainerBuilderSettings> {

    public GuiBuilderTools(ContainerBuilderSettings container) {
        super(container, Component.empty());
    }

}
