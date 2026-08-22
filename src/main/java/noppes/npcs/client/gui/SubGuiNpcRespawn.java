package noppes.npcs.client.gui;

import net.minecraft.network.chat.Component;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcRespawn extends GuiBasic implements ITextfieldListener {

	protected final DataStats stats;

	public SubGuiNpcRespawn(DataStats statsIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;
		closeOnEsc = true;

		stats = statsIn;
	}

	@Override
	public void initGui() {
		super.initGui();
		addLabel(0, guiLeft + 5, guiTop + 35, "stats.respawn")
				.setHoverTexts("guihint.npcrespawn");
		Component mes = Component.translatable("stats.hover.respawn.type")
				.append(Component.translatable("stats.hover.respawn.type." + stats.spawnCycle));
		addButton(0, guiLeft + 122, guiTop + 30, true, stats.spawnCycle,
				"gui.yes", "gui.day", "gui.night", "gui.no", "stats.naturally")
				.setSize(80, 20)
				.setHoverTexts(mes);
		if (stats.respawnTime > 0) {
			addLabel(3, guiLeft + 5, guiTop + 57, "gui.time")
					.setHoverTexts("guihint.npcrespawntime");
			addTextField(2, guiLeft + 122, guiTop + 53, 50, 18, stats.respawnTime)
					.setMinMaxDefault(1, Integer.MAX_VALUE, 20)
					.setHoverTexts("stats.hover.respawn.time");
			addLabel(4, guiLeft + 4, guiTop + 79, "stats.deadbody")
					.setHoverTexts("guihint.npchidebody")
					.setHoverTexts("stats.hover.respawn.body");
			addYesNo(4, guiLeft + 122, guiTop + 74, stats.hideKilledBody)
					.setSize(60, 20);
		}
		addButton(66, guiLeft + 82, guiTop + 190,  "gui.done")
				.setSize(98, 20)
				.setHoverTexts("hover.back");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 0: {
				stats.spawnCycle = button.getValue();
				if (stats.spawnCycle == 3 || stats.spawnCycle == 4) { stats.respawnTime = 0; }
				else { stats.respawnTime = 20; }
				initGui();
				break;
			}
			case 4: {
				stats.hideKilledBody = (button.getValue() == 1);
				break;
			}
			case 66: onClose(); break;
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (textField.id == 2) { stats.respawnTime = textField.getInteger(); }
	}

}
