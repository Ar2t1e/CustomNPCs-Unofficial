package noppes.npcs.client.gui.availability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumAvailabilityPlayerName;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

public class SubGuiNpcAvailabilityNames
		extends GuiNPCInterface
		implements ICustomScrollListener {

	private final Availability availability;
	private final Map<Component, EnumAvailabilityPlayerName> data = new HashMap<>();
	protected GuiCustomScrollNop scroll;
	protected Component select = Component.empty();

	public SubGuiNpcAvailabilityNames(Availability availabilityIn) {
		setBackground("menubg.png");
		imageWidth = 256;
		imageHeight = 217;
		closeOnEsc = true;

		availability = availabilityIn;
	}

	@Override
	public void buttonEvent(GuiButtonNop guiButton) {
		switch (guiButton.id) {
			case 0: {
				if (select.getString().isEmpty()) { return; }
				EnumAvailabilityPlayerName eapn = EnumAvailabilityPlayerName.values()[guiButton.getValue()];
				availability.playerNames.put(select.getString(), eapn);
				initGui();
				break;
			}
			case 1: {
				setSubGui(new SubGuiEditText(0, select.getString()).setHoverTexts(Component.translatable("availability.hover.player.name")));
				break;
			}
			case 2: {
				if (select.getString().isEmpty()) { return; }
				availability.playerNames.remove(select.getString());
				select = Component.empty();
				initGui();
				break;
			}
			case 3: {
				save();
				initGui();
				break;
			}
			case 66: onClose(); break;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		boolean isSelect = !select.getString().isEmpty();
		// title
		addLabel(1, guiLeft + 6, guiTop + 4, "availability.available.4")
				.setSize(imageWidth - 12, 12)
				.setCenter(imageWidth - 12);
		// exit
		addButton(66, guiLeft + 6, guiTop + 192, "gui.done")
				.setSize(70, 20)
				.setHoverTexts("hover.back");
		// data
		if (scroll == null) { scroll = addScroll(6).setSize(imageWidth - 12, imageHeight - 66); }
		data.clear();
		for (String name : availability.playerNames.keySet()) { data.put(Component.literal(name), availability.playerNames.get(name)); }
		if (isSelect) {
			boolean found = false;
			for (Component line : data.keySet()) {
				if (line.getString().equals(select.getString())) {
					found= true;
					break;
				}
			}
			if (!found) {
				select = Component.empty();
				isSelect = false;
			}
		}
		scroll.setNormalList(new ArrayList<>(data.keySet()));
		if (isSelect) { scroll.setSelected(select); }
		else { scroll.setSelect(-1); }
		add(scroll.setPos(guiLeft + 6, guiTop + 14));
		int p = 0;
		if (isSelect) { p = data.get(select).ordinal(); }
		// type
		addButton(0, guiLeft + 6, guiTop + imageHeight - 46, false, p, "availability.only", "availability.except")
				.setSize(70, 20)
				.setHoverTexts("availability.hover.name." + p);
		// select
		GuiButtonNop button = addButton(1, guiLeft + 78, guiTop + imageHeight - 46, "availability.select")
				.setSize(150, 20)
				.setHoverTexts("availability.hover.player.name");
		if (isSelect) { button.setDisplayText(select); }
		// del
		addButton(2, guiLeft + 230, guiTop + imageHeight - 46, "X")
				.setSize(20, 20)
				.setIsEnabled(isSelect)
				.setHoverTexts("availability.hover.remove");
		// extra
		addButton(3, guiLeft + imageWidth - 76, guiTop + 192, "availability.more")
				.setSize(70, 20)
				.setIsEnabled(isSelect)
				.setHoverTexts("availability.hover.more");
	}

	@Override
	public void save() {
		if (select.getString().isEmpty()) { return; }
		EnumAvailabilityPlayerName eapn = EnumAvailabilityPlayerName.values()[getButton(0).getValue()];
		availability.playerNames.put(select.getString(), eapn);
		select = Component.empty();
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		select = scroll.getNormalSelected();
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
		SubGuiEditText subGui = new SubGuiEditText(0, select.getString())
				.setHoverTexts(Component.translatable("availability.hover.player.name"));
		setSubGui(subGui);
	}

	@Override
	public void subGuiClosed(GuiScreen subgui) {
		if (subgui instanceof SubGuiEditText && !((SubGuiEditText) subgui).cancelled) {
			EnumAvailabilityPlayerName eapn = EnumAvailabilityPlayerName.Only;
			if (!select.getString().isEmpty()) {
				eapn = data.get(select);
				availability.playerNames.remove(select.getString());
			}
			select = Component.literal(((SubGuiEditText) subgui).text[0]);
			availability.playerNames.put(select.getString(), eapn);
			initGui();
		}
	}

}
