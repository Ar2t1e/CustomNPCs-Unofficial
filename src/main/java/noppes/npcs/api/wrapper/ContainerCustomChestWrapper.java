package noppes.npcs.api.wrapper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import noppes.npcs.api.IContainerCustomChest;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketCustomChestName;

public class ContainerCustomChestWrapper extends ContainerWrapper implements IContainerCustomChest {

	public String name = "";
	public ScriptContainer script = null;

	public ContainerCustomChestWrapper(Container container) { super(container); }

	@Override
	public String getName() { return name; }

	@Override
	public void setName(String nameIn) {
		if (nameIn == null) { nameIn = ""; }
		if (!name.equals(nameIn)) {
			name = nameIn;
			Packets.sendDelayed((EntityPlayerMP) ((ContainerNpcInterface) getMCContainer()).player, new PacketCustomChestName(name), 10);
		}
	}

}
