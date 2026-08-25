package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.List;

import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class GuiCreationLoad
		extends GuiCreationScreenInterface
		implements ICustomScrollListener {

	protected final List<String> list = new ArrayList<>();
	protected GuiCustomScrollNop scroll;

	public GuiCreationLoad(EntityNPCInterface npc) {
		super(npc);

		active = 5;
		xOffset = 60;
		PresetController.instance.load();
	}

	@Override
	public void buttonEvent(@Nonnull GuiButtonNop button) {
		if (button.id == 10 && scroll.hasSelected()) {
			PresetController.instance.removePreset(scroll.getSelected());
			initGui();
		}
		super.buttonEvent(button);
	}

	@Override
	public void initGui() {
		super.initGui();
		if (scroll == null) { scroll = addScroll(0); }
		list.clear();
		for (Preset preset : PresetController.instance.presets.values()) { list.add(preset.name); }
		add(scroll.setPos(guiLeft, guiTop + 45)
				.setList(list)
				.setSize(100, imageHeight - 96));
		addButton(10, guiLeft, guiTop + imageHeight - 46, "gui.remove")
				.setSize(120, 20);
	}

	@Override
	public void scrollClicked(GuiCustomScrollNop scroll) {
		Preset preset = PresetController.instance.getPreset(scroll.getSelected());
		playerdata.load(preset.data.save());
		initGui();
	}

	@Override
	public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
