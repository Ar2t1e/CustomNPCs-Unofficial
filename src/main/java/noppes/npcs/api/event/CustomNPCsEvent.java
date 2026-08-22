package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.WrapperNpcAPI;

import javax.annotation.Nonnull;

public class CustomNPCsEvent extends Event {

	public final @Nonnull NpcAPI API;

	public CustomNPCsEvent() {
		NpcAPI api = NpcAPI.Instance();
		if (api == null) { api = WrapperNpcAPI.Instance(); }
		API = api;
	}

}
