package noppes.npcs.api;

import net.minecraft.entity.Entity;
import noppes.npcs.api.interfaces.IgnoreForAPI;

@IgnoreForAPI
public interface IChatMessages {

	void addMessage(String message, Entity npc);

	void renderMessages(double x, double y, double z, float height, boolean inRange, boolean isPlayer);

}
