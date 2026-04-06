package noppes.npcs.client.gui.player;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import noppes.npcs.containers.ContainerChestCustom;
import noppes.npcs.shared.client.gui.GuiBasicContainer;

public class GuiCustomContainer extends GuiBasicContainer<ContainerChestCustom> {

    public GuiCustomContainer(ContainerChestCustom cont, Inventory inv, Component titleIn) {
        super(cont, inv, titleIn);
    }

}
