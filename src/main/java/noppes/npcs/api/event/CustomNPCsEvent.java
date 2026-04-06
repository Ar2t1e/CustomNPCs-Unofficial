package noppes.npcs.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.NpcAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomNPCsEvent extends Event {

	public final NpcAPI API = NpcAPI.Instance();

}
