package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.CustomBlocks;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptBlockHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.data.DataTimers;

import javax.annotation.Nonnull;

public class TileScriptedDoor extends TileDoor implements ITickable, IScriptBlockHandler {

	protected IBlock blockDummy = null;
	public List<ScriptContainer> scripts = new ArrayList<>();
	public String scriptLanguage = "ECMAScript";
	public DataTimers timers = new DataTimers(this);
	public boolean enabled = false;
	public int newPower = 0;
	public int prevPower = 0;
	private short tickCount = 0;
	public float blockHardness = 5.0F;
	public float blockResistance = 10.0F;
	public long lastInited = -1L;

	// New from Unofficial (BetaZavr)
	public String closeSound = "";
	public String openSound = "";

	@Override
	public void clearConsole() {
		for (ScriptContainer script : getScripts()) { script.console.clear(); }
	}

	@Override
	public IBlock getBlock() {
		if (blockDummy == null) { blockDummy = new BlockScriptedDoorWrapper(getWorld(), CustomBlocks.scripted_door.getDefaultState(), getPos()); }
		return blockDummy;
	}

	@Override
	public TreeMap<Long, String> getConsoleText() {
		TreeMap<Long, String> map = new TreeMap<>();
		int tab = 0;
		for (ScriptContainer script : getScripts()) {
			++tab;
			for (Map.Entry<Long, String> entry : script.console.entrySet()) {
				String log;
				if (map.containsKey(entry.getKey())) { log = map.get(entry.getKey()) + "\n\n" + "ScriptTab " + tab + ":\n" + entry.getValue(); }
				else { log = " ScriptTab " + tab + ":\n" + entry.getValue(); }
				map.put(entry.getKey(), log);
			}
		}
		return map;
	}

	@Override
	public void clearConsoleText(Long key) {
		for (ScriptContainer script : getScripts()) { script.console.remove(key); }
	}

	@Override
	public boolean getEnabled() { return enabled; }

	@Override
	public String getLanguage() { return scriptLanguage; }

	public NBTTagCompound getNBT(NBTTagCompound compound) {
		compound.setTag("Scripts", NBTTags.nbtScript(scripts));
		compound.setString("ScriptLanguage", scriptLanguage);
		compound.setString("CloseSound", closeSound);
		compound.setString("OpenSound", openSound);
		compound.setBoolean("ScriptEnabled", enabled);
		compound.setInteger("BlockPrevPower", prevPower);
		compound.setFloat("BlockHardness", blockHardness);
		compound.setFloat("BlockResistance", blockResistance);
		return compound;
	}

	@Override
	public List<ScriptContainer> getScripts() { return scripts; }

	@Override
	public boolean isClient() { return getWorld().isRemote; }

	@Override
	public boolean isEnabled() { return enabled && ScriptController.HasStart && !world.isRemote; }

	@Override
	public Component noticeString(String type, Object event) {
		Component message = Component.literal("Scripted Door")
				.withStyle(TextFormatting.DARK_GRAY);
		if (type != null) {
			message.append(Component.literal(" hook \"").withStyle(TextFormatting.DARK_GRAY))
					.append(Component.literal(type).withStyle(TextFormatting.GRAY))
					.append(Component.literal("\"; ").withStyle(TextFormatting.DARK_GRAY));
		}
		else { message.append(Component.literal("; ").withStyle(TextFormatting.DARK_GRAY)); }
		int dimID = world == null ? 0 : world.provider.getDimension();
		double x = Math.round(pos.getX() * 100.0d) / 100.0d;
		double y = Math.round(pos.getY() * 100.0d) / 100.0d;
		double z = Math.round(pos.getZ() * 100.0d) / 100.0d;
		Component posClick = Component.literal("dimension ID:" + dimID + "; X:" + x + "; Y:" + y + "; Z:" + z);
		posClick.getStyle().setColor(TextFormatting.BLUE)
				.setUnderlined(true)
				.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/noppes world tp @p " + dimID + " " + x + " " + (y + 1) + " "+z))
				.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("script.hover.error.pos.tp")));
		message.append(Component.literal("in ").withStyle(TextFormatting.DARK_GRAY))
				.append(posClick);
		return message.append(Component.literal("; Side: " + (isClient() ? "Client" : "Server")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public void readFromNBT(@Nonnull NBTTagCompound compound) {
		super.readFromNBT(compound);
		setNBT(compound);
		timers.readFromNBT(compound);
	}

	@Override
	public void runScript(String type, Event event) {
		if (!isEnabled()) { return; }
		if (ScriptController.Instance.lastLoaded > lastInited) {
			lastInited = ScriptController.Instance.lastLoaded;
			if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) { EventHooks.onScriptBlockInit(this); }
		}
		for (ScriptContainer script : scripts) { script.run(type, event); }
	}

	@Override
	public void setEnabled(boolean bo) { enabled = bo; }

	@Override
	public void setLanguage(String lang) { scriptLanguage = lang; }

	@Override
	public void setLastInited(long timeMC) { lastInited = timeMC; }

	@Override
	public void init() { lastInited = -1; }

	public void setNBT(NBTTagCompound compound) {
		scripts = NBTTags.getScript(compound.getTagList("Scripts", 10), this);
		scriptLanguage = compound.getString("ScriptLanguage");
		closeSound = compound.getString("CloseSound");
		openSound = compound.getString("OpenSound");
		enabled = compound.getBoolean("ScriptEnabled");
		prevPower = compound.getInteger("BlockPrevPower");
		if (compound.hasKey("BlockHardness")) {
			blockHardness = compound.getFloat("BlockHardness");
			blockResistance = compound.getFloat("BlockResistance");
		}
	}

	@Override
	public void update() {
		super.update();
		++tickCount;
		if (prevPower != newPower) {
			EventHooks.onScriptBlockRedstonePower(this, prevPower, newPower);
			prevPower = newPower;
		}
		timers.update();
		if (tickCount >= 10) {
			if (isEnabled()) {
				ScriptController.Instance.tryAdd(1, this);
				EventHooks.onScriptBlockUpdate(this);
			}
			tickCount = 0;
		}
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
		getNBT(compound);
		timers.writeToNBT(compound);
		return super.writeToNBT(compound);
	}

	// New from Unofficial (BetaZavr)
	public String getSound(boolean isOpen) { return isOpen ? openSound : closeSound; }

	public void setSound(boolean isOpen, String song) {
		if (song == null) { song = ""; }
		if (isOpen) { openSound = song; }
		else { closeSound = song; }
	}

}
