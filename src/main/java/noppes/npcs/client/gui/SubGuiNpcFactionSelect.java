package noppes.npcs.client.gui;

import java.util.*;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.client.gui.global.GuiNpcManageFactions;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.util.Util;

public class SubGuiNpcFactionSelect extends GuiNPCInterface implements ICustomScrollListener {

	protected final String name;
	protected final HashMap<Component, Integer> base;
	protected final Map<Component, Integer> data = new LinkedHashMap<>();
	protected GuiCustomScrollNop scrollHostileFactions;

	public final int id;
	public HashSet<Integer> selectFactions;

	public SubGuiNpcFactionSelect(int idIn, String nameIn, HashSet<Integer> setFactions, HashMap<Component, Integer> baseIn) {
		super();
		setBackground("menubg.png");
		imageWidth = 171;
		imageHeight = 217;
		closeOnEsc = true;

		id = idIn;
		name = nameIn;
		base = baseIn;
		selectFactions = new HashSet<>(setFactions);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
		switch (button.id) {
			case 14: {
				GuiNpcManageFactions.sortByName = ((GuiCheckBoxNop) button).selected();
				button.setHoverTexts(Component.translatable("hover.sort",
						Component.translatable("global.factions").getFormattedText(),
						button.getMessage().getFormattedText()));
				break;
			}
			case 66: onClose(); break;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		List<Map.Entry<Component, Integer>> newList = new ArrayList<>(base.entrySet());
		newList.sort((f_0, f_1) -> {
			if (GuiNpcManageFactions.sortByName) { return f_0.getKey().getString().compareTo(f_1.getKey().getString()); }
			else { return f_0.getValue().compareTo(f_1.getValue()); }
		});
		HashSet<Component> set = new HashSet<>();
		data.clear();
		for (Map.Entry<Component, Integer> entry : newList) {
			int id = entry.getValue();
			String name = entry.getKey().getString();
			if (name.contains("ID:" + id + " ")) { name = name.substring(name.indexOf(" ") + 1); }
			Component key = Component.empty()
					.append(Component.literal("ID:" + id + " ").withStyle(TextFormatting.GRAY))
					.append(Component.literal(name).withStyle(TextFormatting.RESET));
			data.put(key, id);
			if (key.getString().equals(name)) { continue; }
			if (selectFactions.contains(id)) { set.add(key); }
		}
		if (scrollHostileFactions == null) { scrollHostileFactions = addScroll(1, true).setSize(161, 163); }
		int x = guiLeft + 5;
		int y = guiTop + 5;
		addLabel(0, guiLeft, y, Util.instance.deleteColor(name)).setCenter(imageWidth);
		addLabel(1, x + 1, y += 13, "faction.select");
		add(scrollHostileFactions.setPos(guiLeft + 5, y + 10)
				.setUnsortedList(new ArrayList<>(data.keySet()))
				.setSelectedList(set));
		y = guiTop + imageHeight - 24;
		addButton(66, guiLeft + imageWidth - 49, y, "gui.done")
				.setSize(45, 20)
				.setHoverTexts("hover.back");
		GuiButtonNop checkBox = addCheckBox(14, x, y + 3, "gui.name", "ID", GuiNpcManageFactions.sortByName)
				.setSize(60, 12);
		checkBox.setHoverTexts(Component.translatable("hover.sort",
				Component.translatable("global.factions").getFormattedText(),
				checkBox.getMessage().getFormattedText()));
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		if (scroll.id == 1) {
			HashSet<Integer> set = new HashSet<>();
			List<Component> list = scroll.getSelectedList();
			HashSet<Component> newList = new HashSet<>();
			for (Component key : data.keySet()) {
				int id = data.get(key);
				if (!list.contains(key)) { continue; }
				set.add(id);
				newList.add(key);
			}
			selectFactions = set;
			if (list.size() != newList.size()) { scroll.setSelectedList(newList); }
		}
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
