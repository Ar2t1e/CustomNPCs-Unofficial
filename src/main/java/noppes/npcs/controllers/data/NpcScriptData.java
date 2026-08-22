package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.util.CustomNPCsScheduler;

public class NpcScriptData
extends BaseScriptData {
	
	@Override
	public Component noticeString(String type, Object event) {
		Component message = Component.literal("NPC's Scripts").withStyle(TextFormatting.DARK_GRAY);
		if (type != null) {
			message.append(Component.literal(" hook \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(type).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; ").withStyle(TextFormatting.DARK_GRAY));
		}
		else { message.append(Component.literal("; ").withStyle(TextFormatting.DARK_GRAY)); }
		boolean bo = event instanceof NpcEvent && ((NpcEvent) event).npc != null;
		if (bo) {
			String dimID = ((NpcEvent) event).npc.getWorld() == null ? "0" : "" + ((NpcEvent) event).npc.getWorld().getMCWorld().provider.getDimension();
			double x = Math.round(((NpcEvent) event).npc.getX() * 100.0d) / 100.0d;
			double y = Math.round(((NpcEvent) event).npc.getY() * 100.0d) / 100.0d;
			double z = Math.round(((NpcEvent) event).npc.getZ() * 100.0d) / 100.0d;
			Component posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
			posClick.setStyle(posClick.getStyle().setColor(TextFormatting.BLUE)
					.setUnderlined(true)
					.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + y + " "+z))
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp").getParent())));
			message.append(Component.literal("NPC \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(((NpcEvent) event).npc.getName()).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; UUID: \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(((NpcEvent) event).npc.getUUID()).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\" in ").withStyle(TextFormatting.DARK_GRAY))
					.append(posClick);
		}
		return message.append(Component.literal((bo ? "; " : "") +"Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}
	
	@Override
	public void runScript(String type, Event event) {
		if (isEnabled()) {
			CustomNPCsScheduler.runTack(() -> {
				try {
					if (ScriptController.Instance.lastLoaded > lastInited) {
						lastInited = ScriptController.Instance.lastLoaded;
						if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) {
							EventHooks.onNPCsInit(this);
						}
					}
					for (ScriptContainer script : scripts) { script.run(type, event); }
				} catch (Exception e) { LogWriter.error(e); }
			});
		}
	}


	public void load(NBTTagCompound compound) {
		super.load(compound);
		EventHooks.onNPCsInit(this);
	}

}
