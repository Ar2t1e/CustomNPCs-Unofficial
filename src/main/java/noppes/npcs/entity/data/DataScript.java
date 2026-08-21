package noppes.npcs.entity.data;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.BaseScriptData;
import noppes.npcs.entity.EntityNPCInterface;

public class DataScript extends BaseScriptData {

	protected final EntityNPCInterface npc;

	public DataScript(EntityNPCInterface npcIn) { npc = npcIn; }

	@Override
	public boolean isClient() {
		if (npc == null || npc.world == null) { return super.isClient(); }
		return npc.world.isRemote;
	}

	@Override
	public Component noticeString(String type, Object event) {
		Component message = Component.literal("NPC Script").withStyle(TextFormatting.DARK_GRAY);
		if (type != null) {
			message.append(Component.literal(" hook \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(type).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; ").withStyle(TextFormatting.DARK_GRAY));
		}
		else { message.append(Component.literal("; ").withStyle(TextFormatting.DARK_GRAY)); }
		message.append(Component.literal("name \"").withStyle(TextFormatting.DARK_GRAY))
				.append(Component.literal(npc != null ? npc.getName() : "null").withStyle(TextFormatting.GRAY))
				.append(Component.literal("\"; UUID: \"").withStyle(TextFormatting.DARK_GRAY))
				.append(Component.literal(npc != null ? npc.getUniqueID().toString() : "null").withStyle(TextFormatting.GRAY))
				.append(Component.literal("\" in ").withStyle(TextFormatting.DARK_GRAY));
		String dimID = npc == null || npc.world == null ? "overworld" : "" + npc.world.provider.getDimension();
		double x = npc != null ? Math.round(npc.posX * 100.0d) / 100.0d : 0.0d;
		double y = npc != null ? Math.round(npc.posY * 100.0d) / 100.0d : 0.0d;
		double z = npc != null ? Math.round(npc.posZ * 100.0d) / 100.0d : 0.0d;
		Component posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
		Style style = posClick.getStyle().setColor(TextFormatting.BLUE)
				.setUnderlined(true)
				.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + y + " "+z))
				.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp").getParent()));
		posClick.setStyle(style);
		return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public void runScript(String type, Event event) {
		if (isEnabled()) {
			if (ScriptController.Instance.lastLoaded > lastInited) {
				lastInited = ScriptController.Instance.lastLoaded;
				if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) { EventHooks.onNPCInit(npc); }
			}
			for (ScriptContainer script : scripts) { script.run(type, event); }
		}
	}

	public EntityNPCInterface getNPC() { return npc; }

}
