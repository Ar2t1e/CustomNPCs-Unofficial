package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class SubGuiNPCLinesEdit extends GuiNPCInterface
		implements ICustomScrollListener, ITextfieldListener {

	protected final Map<Component, Integer> data = new LinkedHashMap<>();
	protected GuiCustomScrollNop scroll;
	protected Component select = Component.empty();
	public final int id;
	public Lines lines;

	public SubGuiNPCLinesEdit(int idIn, EntityNPCInterface npc, Lines linesIn, String titleIn) {
		super(npc);
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 217;
		closeOnEsc = true;
		id = idIn;

		lines = linesIn.copy();
		title = titleIn == null ? Component.empty() : Component.translatable(titleIn);
		Packets.sendServer(new SPacketMenuGet(EnumMenuType.ADVANCED));
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		if (select.getString().isEmpty() && scroll.hasSelected()) { select = scroll.getNormalSelected(); }
		switch (button.id) {
			case 0: setSubGui(new SubGuiEditText(0, CustomNpcs.DefaultInteractLine)); break; // add
			case 1: {
				if (!data.containsKey(select)) { return; }
				lines.remove(data.get(select));
				if (scroll != null && scroll.hasSelected()) { scroll.setSelect(scroll.getSelectedIndex() - 1); }
				initGui();
				break;
			} // remove
			case 2: {
				if (!data.containsKey(select) || !lines.lines.containsKey(data.get(select))) {
					setSubGui(new SubGuiEditText(0, CustomNpcs.DefaultInteractLine)); // add
					return;
				}
				setSubGui(new SubGuiSoundSelection(this, 0, npc, lines.lines.get(data.get(select)).getSound()));
				break;
			} // sel sound
			case 66: onClose(); break;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		data.clear();
		int p = 0;
		Component t = Component.empty()
				.append(Component.translatable("parameter.position").withStyle(TextFormatting.GRAY))
				.append(Component.literal(": ").withStyle(TextFormatting.GRAY));
		Component m = Component.empty().append(Component.translatable("parameter.iline.text").withStyle(TextFormatting.GRAY))
				.append(Component.literal(":").withStyle(TextFormatting.GRAY));
		Component s = Component.empty().append(Component.translatable("parameter.sound.name").withStyle(TextFormatting.GRAY))
				.append(Component.literal(":").withStyle(TextFormatting.GRAY));
		List<Component> suffixes = new ArrayList<>();
		LinkedHashMap<Integer, List<Component>> hts= new LinkedHashMap<>();
		for (int i : lines.lines.keySet()) {
			Line l = lines.lines.get(i);
			data.put(Component.empty()
					.append(Component.literal(i + ": ").withStyle(TextFormatting.GRAY))
					.append(Component.literal(l.getText()).withStyle(TextFormatting.RESET)), i);
			List<Component> hover = new ArrayList<>();
			hover.add(t.copy().append(Component.literal("" + i).withStyle(TextFormatting.WHITE)));
			hover.add(m);
			hover.add(Component.literal(l.getText()));
			if (!l.getSound().isEmpty()) {
				hover.add(s);
				hover.add(Component.literal(l.getSound()));
				suffixes.add(Component.empty()
						.append(Component.literal("[").withStyle(TextFormatting.GRAY))
						.append(Component.literal("S").withStyle(TextFormatting.AQUA))
						.append(Component.literal("]").withStyle(TextFormatting.GRAY)));
			} else {
				suffixes.add(Component.empty());
			}
			hts.put(p, hover);
		}
		if (scroll == null) { scroll = addScroll(0).setSize(imageWidth - 12, imageHeight - 85); }
		List<Component> list = new ArrayList<>(data.keySet());
		Line line = null;
		if (!select.getString().isEmpty()) {
			boolean hasInList = false;
			for (Component c : list) {
				if (Util.instance.deleteColor(c.getString()).equals(Util.instance.deleteColor(select.getString()))) {
					select = c;
					line = lines.lines.get(data.get(select));
					scroll.setSelected(select);
					hasInList = true;
				}
			}
			if (!hasInList) { select = Component.empty(); }
		}
		scroll.setUnsortedList(list)
				.setSuffixes(suffixes).setHoverTexts(hts);
		add(scroll.setPos(guiLeft + 6, guiTop + 14));
		// title
		int lId = 0;
		addLabel(lId++, guiLeft, guiTop + 4, title).setCenter(imageWidth);
		// text
		int x = guiLeft + 6;
		int y = guiTop + scroll.height + 38;
		// text
		addLabel(lId++, x, y + 5, Component.translatable("gui.message").append(":"))
				.setSize(60, 10);
		addTextField(0, x + 63, y + 1, 180, 18, line == null ? "" : line.getText())
				.setHoverTexts("lines.hover.text");
		// sound
		addLabel(lId, x, (y += 22) + 5, Component.translatable("advanced.sounds").append(":"))
				.setSize(54, 10);
		addTextField(1, x + 37, y + 1, 155, 18, line == null ? "" : line.getSound())
				.setHoverTexts("lines.hover.sound");
		addButton(2, guiLeft + imageWidth - 55, y, "availability.select")
				.setSize(50, 20)
				.setHoverTexts("bard.hover.select");
		// select
		addButton(0, guiLeft + imageWidth - 107, y += 22, "gui.add")
				.setSize(80, 20)
				.setHoverTexts("lines.hover.add");
		addButton(1, guiLeft + imageWidth - 25, y, "X")
				.setSize(20, 20)
				.setHoverTexts("lines.hover.remove");
		// back
		addButton(66, x, y, "gui.done")
				.setSize(50, 20)
				.setHoverTexts("hover.back");
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (!data.containsKey(scroll.getNormalSelected())) { return; }
		select = scroll.getNormalSelected();
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof SubGuiEditText) {
			if (((SubGuiEditText) subgui).cancelled || ((SubGuiEditText) subgui).text[0].isEmpty()) { return; }
			Line line = new Line(((SubGuiEditText) subgui).text[0]);
			lines.correctLines();
			int p = lines.lines.size();
			lines.lines.put(p, line);
			select = Component.empty()
					.append(Component.literal(p + ": ").withStyle(TextFormatting.GRAY))
					.append(Component.literal(line.getText()).withStyle(TextFormatting.RESET));
			initGui();
		}
		else if (subgui instanceof SubGuiSoundSelection) {
			if (!data.containsKey(select)) { return; }
			if (((SubGuiSoundSelection) subgui).resource == null || !data.containsKey(select) || !lines.lines.containsKey(data.get(select))) { return; }
			lines.lines.get(data.get(select)).setSound(((SubGuiSoundSelection) subgui).resource.toString());
			initGui();
		}
	}

	@Override
	public void unFocused(GuiTextFieldNop textField) {
		if (!hasSubGui() && textField.id == 0) {
			if (!data.containsKey(select) || !lines.lines.containsKey(data.get(select))) { return; }
			lines.lines.get(data.get(select)).setText(textField.getValue());
			select = Component.literal(textField.getValue());
			initGui();
		}
	}

}
