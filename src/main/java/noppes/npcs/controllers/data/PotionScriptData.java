package noppes.npcs.controllers.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.EventHooks;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.scripts.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.util.CustomNPCsScheduler;

public class PotionScriptData
extends BaseScriptData {

	@Override
	public Component noticeString(String type, Object event) {
		return Component.literal("Potion Scripts ").withStyle(TextFormatting.DARK_GRAY)
				.append(super.noticeString(type, event));
	}

	@Override
	public void runScript(String type, Event event) {
		if (!isEnabled()) { return; }
		CustomNPCsScheduler.runTack(() -> {
			try {
				if (ScriptController.Instance.lastLoaded > this.lastInited) {
					this.lastInited = ScriptController.Instance.lastLoaded;
					if (!type.equalsIgnoreCase(EnumScriptType.INIT.function)) { EventHooks.onPotionInit(this); }
				}
				for (ScriptContainer script : scripts) { script.run(type, event); }
			} catch (Exception e) { LogWriter.error("Error run script: ", e); }
		});
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);

		if (scripts.isEmpty() || scripts.get(0).script.isEmpty()) {
			ScriptContainer script = new ScriptContainer(this);
			char chr = Character.toChars(0x000A)[0];
			script.script = "// IPotion.getCustomName() - String (custom potion name)" + chr
					+ "// IPotion.getNbt() - INbt (nbt data)" + chr + "function isReady(event) {" + chr
					+ "  /* event.potion - IPotion" + chr + "     event.duration - int (ticks)" + chr
					+ "     event.amplifier - int (potion power) */" + chr + "}" + chr
					+ "function performEffect(event) {" + chr + "  /* event.potion - IPotion" + chr
					+ "     event.entity - IEntity" + chr + "     event.amplifier - int (potion power) */" + chr + "}"
					+ chr + "function affectEntity(event) {" + chr + "  /* event.potion - IPotion" + chr
					+ "     event.entity - IEntity" + chr + "     event.source - IEntity" + chr
					+ "     event.indirectSource - IEntity" + chr + "     event.amplifier - int (potion power)" + chr
					+ "     event.health - double (health value) */" + chr + "}" + chr + "function endEffect(event) {"
					+ chr + "  /* event.potion - IPotion" + chr + "     event.entity - IEntity" + chr
					+ "     event.amplifier - int (potion power) */" + chr + "}";
			if (scripts.isEmpty()) { scripts.add(script); }
			else {
				scripts.remove(0);
				scripts.add(0, script);
			}
		}
		EventHooks.onPotionInit(this);
	}
	
}