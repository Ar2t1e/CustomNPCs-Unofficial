package noppes.npcs.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.entity.data.DataRanged;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcRangeProperties extends GuiBasic implements ITextfieldListener {

	protected final DataRanged ranged;
	protected final DataStats stats;
	protected GuiTextFieldNop soundSelected;
	protected final Object[] fireType = new Object[] { "gui.no", "gui.whendistant", "gui.whenhidden" };

	public SubGuiNpcRangeProperties(DataStats statsIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 216;

		stats = statsIn;
		ranged = statsIn.ranged;
	}

	@Override
	public void initGui() {
		super.initGui();
		int lId = 0;
		int x0 = guiLeft + 5;
		int x1 = guiLeft + 80;
		int x2 = guiLeft + 135;
		int x3 = guiLeft + 200;
		int x4 = guiLeft + 187;
		int y = guiTop + 4;
		// accuracy
		addLabel(lId++, x0, y + 5, "stats.accuracy").setSize(73, 12);
		addTextField(1, x1, y, 50, 18, ranged.getAccuracy())
				.setMinMaxDefault(0, 100, 90)
				.setHoverTexts("stats.hover.attack.accuracy");
		// shotCount
		addLabel(lId++, x2, y + 5, "stats.burstcount").setSize(73, 12);
		addTextField(8, x3, y, 50, 18, ranged.getShotCount())
				.setMinMaxDefault(1, 10, 1)
				.setHoverTexts("stats.hover.shot.count");
		// ranged range
		y += 22;
		addLabel(lId++, x0, y + 5, "gui.range").setSize(73, 12);
		addTextField(2, x1, y, 50, 18, ranged.getRange())
				.setMinMaxDefault(2.0d, 64.0d, ranged.getRange())
				.setHoverTexts("stats.hover.attack.distance");
		// melee range
		addLabel(lId++, x2, y + 5, "stats.meleerange").setSize(73, 12);
		addTextField(9, x3, y, 30, 20, ranged.getMeleeRange())
				.setMinMaxDefault(0, stats.aggroRange, 5)
				.setHoverTexts("stats.hover.attack.range");
		// ranged min delay
		y += 22;
		addLabel(lId++, x0, y + 5, "stats.mindelay").setSize(73, 12);
		addTextField(3, x1, y, 50, 18, ranged.getDelayMin())
				.setMinMaxDefault(1, 9999, 20)
				.setHoverTexts("stats.hover.attack.min.time");
		// ranged max delay
		addLabel(lId++, x2, y + 5, "stats.maxdelay").setSize(73, 12);
		addTextField(4, x3, y, 50, 18, ranged.getDelayMax())
				.setMinMaxDefault(1, 9999, 20)
				.setHoverTexts("stats.hover.attack.max.time");
		// shot count
		y += 22;
		addLabel(lId++, x0, y + 5, "stats.shotcount").setSize(73, 12);
		addTextField(6, x1, y, 50, 18, ranged.getBurst() + "")
				.setMinMaxDefault(1, 100, 20)
				.setHoverTexts("stats.hover.shot.amount");
		// shot speed
		addLabel(lId++, x2, y + 5, "stats.burstspeed").setSize(73, 12);
		addTextField(5, x3, y, 50, 18, ranged.getBurstDelay() + "")
				.setMinMaxDefault(1, 30, 5)
				.setHoverTexts("stats.hover.shot.speed");
		// fire sound
		y += 22;
		addTextField(7, x1, y, 100, 20, ranged.getSound(0))
				.setHoverTexts("stats.hover.sound.shot");
		addButton(7, x4, y, "mco.template.button.select")
				.setSize(60, 20)
				.setHoverTexts("hover.set");
		addLabel(lId++, x0, y + 5, "stats.firesound").setSize(73, 12);
		// hitting sound
		y += 22;
		addTextField(11, x1, y, 100, 20, ranged.getSound(1))
				.setHoverTexts("stats.hover.sound.hurt");
		addButton(11, x4, y, "mco.template.button.select")
				.setSize(60, 20)
				.setHoverTexts("hover.set");
		addLabel(lId++, x0, y + 5, "stats.hittingsound").setSize(73, 12);
		// hit sound
		y += 22;
		addTextField(10, x1, y, 100, 20, ranged.getSound(2))
				.setHoverTexts("stats.hover.sound.live");
		addButton(10, x4, y, "mco.template.button.select")
				.setSize(60, 20)
				.setHoverTexts("hover.set");
		addLabel(lId++, x0, y + 5, "stats.hitsound").setSize(73, 12);
		// aim while shooting
		y += 22;
		addYesNo(9, x1 + 20, y, ranged.getHasAimAnimation())
				.setHoverTexts("stats.hover.aim");
		addLabel(lId++, x0, y + 5, "stats.aimWhileShooting").setSize(73, 12);
		// indirect
		y += 22;
		Component hover = Component.translatable("stats.hover.availability")
				.append(Component.translatable("stats.hover.availability." + ranged.getFireType(),
						Component.translatable("" + fireType[ranged.getFireType()]).getFormattedText(),
						(ranged.getRange() / 2.0d)));
		if (ranged.getFireType() != 0) { hover.append(Component.translatable("stats.hover.availability.3")); }
		addButton(13, x1 + 20, y, true, ranged.getFireType(), fireType)
				.setSize(80, 20)
				.setHoverTexts(hover);
		addLabel(lId, x0, y + 5, "stats.indirect").setSize(73, 12);
		// exit
		addButton(66, x4 + 3, guiTop + 190, "gui.done")
				.setSize(60, 20)
				.setHoverTexts("hover.back");
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 7: {
				soundSelected = getTextField(7);
				setSubGui(new SubGuiSoundSelection(this, 0, null, soundSelected.getValue()));
				break;
			}
			case 9: {
				ranged.setHasAimAnimation(((GuiButtonYesNo) button).getBoolean());
				break;
			}
			case 10: {
				soundSelected = getTextField(10);
				setSubGui(new SubGuiSoundSelection(this, 0, null, soundSelected.getValue()));
				break;
			}
			case 11: {
				soundSelected = getTextField(11);
				setSubGui(new SubGuiSoundSelection(this, 0, null, soundSelected.getValue()));
				break;
			}
			case 13: {
				ranged.setFireType(button.getValue());
				Component hover = Component.translatable("stats.hover.availability")
						.append(Component.translatable("stats.hover.availability." + ranged.getFireType(),
								Component.translatable("" + fireType[ranged.getFireType()]).getFormattedText(),
								(ranged.getRange() / 2.0d)));
				if (ranged.getFireType() != 0) { hover.append(Component.translatable("stats.hover.availability.3")); }
				button.setHoverTexts(hover);
				break;
			}
			case 66: onClose(); break;
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		switch (textField.id) {
			case 1: ranged.setAccuracy(textField.getInteger()); break;
			case 2: ranged.setRange(textField.getFloat()); break;
			case 3: ranged.setDelay(textField.getInteger(), ranged.getDelayMax()); break;
			case 4: ranged.setDelay(ranged.getDelayMin(), textField.getInteger()); break;
			case 5: ranged.setBurstDelay(textField.getInteger()); break;
			case 6: ranged.setBurst(textField.getInteger()); break;
			case 7: ranged.setSound(0, textField.getValue()); break;
			case 8: ranged.setShotCount(textField.getInteger()); break;
			case 9: ranged.setMeleeRange(textField.getInteger()); break;
			case 10: ranged.setSound(2, textField.getValue()); break;
			case 11: ranged.setSound(1, textField.getValue()); break;
		}
		initGui();
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof SubGuiSoundSelection && ((SubGuiSoundSelection) subgui).resource != null) {
			soundSelected.setValue(((SubGuiSoundSelection) subgui).resource.toString());
			unFocused(soundSelected);
		}
	}

}
