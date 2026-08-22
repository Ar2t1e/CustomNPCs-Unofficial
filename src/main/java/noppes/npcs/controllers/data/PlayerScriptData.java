package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.controllers.ScriptController;

public class PlayerScriptData
extends BaseScriptData {

	private static final TreeMap<Long, String> console = new TreeMap<>();
	private static final List<Integer> errored = new ArrayList<>();
	
	private long lastPlayerUpdate = 0L;
	private final EntityPlayer player;
	private IPlayer<?> playerAPI;

	public PlayerScriptData(EntityPlayer playerIn) { player = playerIn; }

	@Override
	public void clear() {
		console.clear();
		errored.clear();
		scripts.clear();
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		console.clear();
		console.putAll(NBTTags.getLongStringMap(compound.getTagList("ScriptConsole", 10)));
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setTag("ScriptConsole", NBTTags.nbtLongStringMap(console));
		return compound;
	}

	@Override
	public void runScript(String type, Event event) {
		if (isEnabled()) {
			ScriptContainer script;
			if (ScriptController.Instance.lastLoaded > lastInited || ScriptController.Instance.lastPlayerUpdate > lastPlayerUpdate) {
				lastInited = ScriptController.Instance.lastLoaded;
				errored.clear();
				if (player != null) {
					scripts.clear();
					for (ScriptContainer scriptContainer : ScriptController.Instance.playerScripts.scripts) {
						script = scriptContainer;
						ScriptContainer s = new ScriptContainer(this);
						s.load(script.save(new NBTTagCompound()));
						scripts.add(s);
					}
				}
				lastPlayerUpdate = ScriptController.Instance.lastPlayerUpdate;
				if (!type.equals(EnumScriptType.INIT.function)) { EventHooks.onPlayerInit(this); }
			}
			for(int i = 0; i < scripts.size(); ++i) {
				script = scripts.get(i);
				if (!errored.contains(i)) {
					script.run(type, event);
					if (script.isErrored()) { errored.add(i); }
					for (Map.Entry<Long, String> entry : script.console.entrySet()) {
						if (!console.containsKey(entry.getKey())) { console.put(entry.getKey(), " tab " + (i + 1) + ":\n" + entry.getValue()); }
					}
					script.console.clear();
				}
			}
			while (console.size() > 40) { console.remove(console.firstKey()); }
		}
	}

	@Override
	public boolean isClient() { return player == null || player.world.isRemote; }

	@Override
	public String getLanguage() {
		return ScriptController.Instance.playerScripts.scriptLanguage;
	}

	@Override
	public void setLanguage(String lang) { ScriptController.Instance.playerScripts.scriptLanguage = lang; }

	@Override
	public Component noticeString(String type, Object event) {
		Component message = Component.literal((player == null ? "Global p" : "P") + "layers script")
				.withStyle(TextFormatting.DARK_GRAY);
		if (type != null) {
			message.append(Component.literal(" hook \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(type).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; ").withStyle(TextFormatting.DARK_GRAY));
		}
		else { message.append(Component.literal("; ").withStyle(TextFormatting.DARK_GRAY)); }
		if (player != null) {
			int dimID = player.world == null ? 0 : player.world.provider.getDimension();
			double x = Math.round(player.posX * 100.0d) / 100.0d;
			double y = Math.round(player.posY * 100.0d) / 100.0d;
			double z = Math.round(player.posZ * 100.0d) / 100.0d;
			Component posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
			posClick.getStyle().setColor(TextFormatting.BLUE)
					.setUnderlined(true)
					.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + y + " "+z))
					.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("script.hover.error.pos.tp")));
			message.append(Component.literal("name: \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(player.getName()).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; UUID: \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(player.getUniqueID().toString()).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\" in ").withStyle(TextFormatting.DARK_GRAY))
					.append(posClick);
		}
		return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public TreeMap<Long, String> getConsoleText() { return console; }

	@Override
	public void clearConsole() {
		PlayerScriptData.console.clear();
	}

	@Override
	public void clearConsoleText(Long key) {
		PlayerScriptData.console.remove(key);
	}

	public IPlayer<?> getIPlayer() {
		if (playerAPI == null && player != null) { playerAPI = new PlayerWrapper<>(player); }
		return playerAPI;
	}

}
